// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import webit.script.Engine;
import webit.script.Template;
import webit.script.core.ast.TemplateAST;
import webit.script.core.ast.statements.PlaceHolderFactory;
import webit.script.core.text.TextStatementFactory;
import webit.script.exceptions.ParseException;
import webit.script.loggers.Logger;
import webit.script.util.ClassLoaderUtil;
import webit.script.util.ExceptionUtil;
import webit.script.util.StringUtil;
import webit.script.util.collection.ArrayStack;
import webit.script.util.collection.Stack;

/**
 * This class implements a skeleton table driven LR parser.
 * @author Zqq
 */
abstract class lr_parser {

    private final static int stackInitialCapacity = 24;

    lr_parser() {
        this._stack = new ArrayStack<Symbol>(stackInitialCapacity);
    }
    /**
     * The parse _stack itself.
     */
    final Stack<Symbol> _stack;
    /**
     * Internal flag to indicate when parser should quit.
     */
    boolean goonParse = false;

    //
    Engine engine;
    Template template;
    TextStatementFactory textStatementFactory;
    PlaceHolderFactory placeHolderFactory;
    NativeFactory nativeFactory;
    Logger logger;
    boolean locateVarForce;
    NativeImportManager nativeImportMgr;
    VariantManager varmgr;
    Map<String, Integer> labelsIndexMap;
    int currentLabelIndex;

    /**
     *
     * @param template Template
     * @return TemplateAST
     * @throws ParseException
     */
    public TemplateAST parseTemplate(final Template template) throws ParseException {
        Lexer lexer = null;
        try {
            lexer = new Lexer(template.resource.openReader());
            this.template = template;
            final Engine _engine;
            this.engine = _engine = template.engine;
            lexer.setTrimCodeBlockBlankLine(_engine.isTrimCodeBlockBlankLine());
            this.logger = _engine.getLogger();
            TextStatementFactory _textStatementFactory;
            this.textStatementFactory = _textStatementFactory = _engine.getTextStatementFactory();
            this.locateVarForce = !_engine.isLooseVar();
            this.placeHolderFactory = new PlaceHolderFactory(_engine.getFilter());
            //
            this.nativeImportMgr = new NativeImportManager();
            this.nativeFactory = _engine.getNativeFactory();
            this.varmgr = new VariantManager(_engine);
            this.labelsIndexMap = new HashMap<String, Integer>();
            this.labelsIndexMap.put(null, 0);
            this.currentLabelIndex = 0;
            //
            _textStatementFactory.startTemplateParser(template);
            Symbol sym = this.parse(lexer);
            _textStatementFactory.finishTemplateParser(template);
            return (TemplateAST) sym.value;
        } catch (Exception e) {
            throw ExceptionUtil.castToParseException(e);
        } finally {
            if (lexer != null) {
                try {
                    lexer.yyclose();
                } catch (IOException ignore) {
                }
            }
        }
    }

    /**
     * Perform a bit of user supplied action code (supplied by generated
     * subclass). Actions are indexed by an internal action number assigned at
     * parser generation time.
     *
     * @param act_num the internal index of the action to be performed.
     * @return Object
     * @throws java.lang.Exception
     */
    abstract Object do_action(int act_num) throws ParseException;

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * Fetch an action from the action table. The table is broken up into rows,
     * one per state (rows are indexed directly by state number). Within each
     * row, a list of index, value pairs are given (as sequential entries in the
     * table), and the list is terminated by a default entry (denoted with a
     * Symbol index of -1). To find the proper entry in a row we do a linear or
     * binary search (depending on the size of the row).
     *
     * @param row actionTable[state]
     * @param id the Symbol index of the action being accessed.
     */
    private short getAction(final short[] row, int sym) {
        short tag;
        int first, last, probe, row_len;
        //final short[] row = actionTable[state];

        /* linear search if we are < 10 entries */
        if ((row_len = row.length) < 20) {
            for (probe = 0; probe < row_len; probe++) {
                /* is this entry labeled with our Symbol or the default? */
                tag = row[probe++];
                if (tag == sym || tag == -1) {
                    /* return the next entry */
                    return row[probe];
                }
            }
        } else {
            /* otherwise binary search */
            first = 0;
            last = ((row_len - 1) >> 1) - 1;  /* leave out trailing default entry */

            int probe_2;
            while (first <= last) {
                probe = (first + last) >> 1;
                probe_2 = probe << 1;
                if (sym == row[probe_2]) {
                    return row[probe_2 + 1];
                } else if (sym > row[probe_2]) {
                    first = probe + 1;
                } else {
                    last = probe - 1;
                }
            }

            /* not found, use the default at the end */
            return row[row_len - 1];
        }

        /* shouldn't happened, but if we run off the end we return the 
         default (error == 0) */
        return 0;
    }

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * Fetch a state from the reduce-goto table. The table is broken up into
     * rows, one per state (rows are indexed directly by state number). Within
     * each row, a list of index, value pairs are given (as sequential entries
     * in the table), and the list is terminated by a default entry (denoted
     * with a Symbol index of -1). To find the proper entry in a row we do a
     * linear search.
     *
     * @param row reduceTable[state]
     * @param id the Symbol index of the entry being accessed.
     */
    private short getReduce(final short[] row, int sym) {
        int probe, len;
        short tag;
        for (probe = 0, len = row.length; probe < len; probe++) {
            /* is this entry labeled with our Symbol or the default? */
            if ((tag = row[probe++]) == sym || tag == -1) {
                /* return the next entry */
                return row[probe];
            }
        }
        /* if we run off the end we return the default (error == -1) */
        return -1;
    }

    /*. . . . . . . . . . . . . . . . . . . . . . . . . . . . . .*/
    /**
     * This method provides the main parsing routine. It returns only when
     * finishParsing() has been called (typically because the parser has
     * accepted, or a fatal error has been reported). See the header
     * documentation for the class regarding how shift/reduce parsers operate
     * and how the various tables are used.
     */
    private Symbol parse(final Lexer myLexer) throws Exception {
        /* the current action code */
        int act;
        Symbol cur_token;
        Symbol currentSymbol;
        final Stack<Symbol> stack;
        (stack = this._stack).clear();
        //stack.push(newSymbol("START", 0, start_state()));
        {
            Symbol START;
            (START = new Symbol(0, null)).state = Parser.START_STATE;
            stack.push(currentSymbol = START);
        }

        final short[][] actionTable = Parser.ACTION_TABLE;
        final short[][] reduceTable = Parser.REDUCE_TABLE;
        final short[][] productionTable = Parser.PRODUCTION_TABLE;
        //final Lexer myLexer = lexer;
        /* get the first token */
        cur_token = myLexer.nextToken();

        /* continue until we are told to stop */
        goonParse = true;
        do {

            /* look up action out of the current state with the current input */
            act = getAction(actionTable[currentSymbol.state], cur_token.id);

            /* decode the action -- > 0 encodes shift */
            if (act > 0) {
                /* shift to the encoded state by pushing it on the _stack */
                cur_token.state = act - 1;
                stack.push(currentSymbol = cur_token);

                /* advance to the next Symbol */
                cur_token = myLexer.nextToken();
            } else if (act < 0) {
                /* if its less than zero, then it encodes a reduce action */
                //reduceAction()
                act = (-act) - 1;
                final int symId, handleSize;
                final Object result = do_action(act);
                final short[] row;
                symId = (row = productionTable[act])[0];
                handleSize = row[1];
                if (handleSize == 0) {
                    currentSymbol = new Symbol(symId, result);
                } else {
                    currentSymbol = new Symbol(symId, result, stack.peek(handleSize - 1)); //position based on left
                        /* pops the handle off the _stack */
                    stack.pops(handleSize);
                }

                /* look up the state to go to from the one popped back to */
                /* shift to that state */
                currentSymbol.state = getReduce(reduceTable[stack.peek().state], symId);
                stack.push(currentSymbol);

            } else {//act == 0
                throw new ParseException(StringUtil.concat("Parser stop at: ", Integer.toString(myLexer.getLine()), "(", Integer.toString(myLexer.getColumn()), ")"), myLexer.getLine(), myLexer.getColumn());
            }
        } while (goonParse);

        return stack.peek();//lhs_sym;
    }

    static short[][] loadFromDataFile(String name) {
        ObjectInputStream in = null;
        try {
            return (short[][]) (in = new ObjectInputStream(ClassLoaderUtil
                    .getDefaultClassLoader()
                    .getResourceAsStream(StringUtil.concat("webit/script/core/Parser$", name, ".data"))))
                    .readObject();
        } catch (IOException e) {
            throw new Error(e);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
