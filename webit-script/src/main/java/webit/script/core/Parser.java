
//----------------------------------------------------
// The following code was generated by CUP v0.12for-WebitScript-only
// Mon Dec 09 15:54:42 CST 2013
//----------------------------------------------------

package webit.script.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import webit.script.asm.AsmMethodCaller;
import webit.script.asm.AsmMethodCallerManager;
import webit.script.core.VariantManager.VarAddress;
import webit.script.core.ast.*;
import webit.script.core.ast.expressions.*;
import webit.script.core.ast.method.*;
import webit.script.core.ast.statments.*;
import webit.script.exceptions.ParseException;
import webit.script.util.ClassNameBand;
import webit.script.util.ClassUtil;
import webit.script.util.StatmentUtil;
import webit.script.util.StringUtil;
import webit.script.util.collection.Stack;

/** CUP v0.12for-WebitScript-only generated parser.
  * @version Mon Dec 09 15:54:42 CST 2013
  */
public class Parser extends lr_parser {



  /** Production table. */
  static final short PRODUCTION_TABLE[][] = loadFromDataFile("Production");

  /** Parse-action table. */
  static final short[][] ACTION_TABLE = loadFromDataFile("Action");

  /** <code>reduce_goto</code> table. */
  static final short[][] REDUCE_TABLE = loadFromDataFile("Reduce");
  /** Indicates start state. */
  final static int START_STATE = 0;



    private int getLabelIndex(String label){
        Integer index;
        if ((index = labelsIndexMap.get(label)) == null) {
            labelsIndexMap.put(label, index = ++currentLabelIndex);
        }
        return index;
    }

    private Expression createContextValue(VarAddress addr, int line, int column) {
        if (addr.isRoot) {
            return new RootContextValue(addr.index, line, column);
        } else if (addr.upstairs == 0) {
            return new CurrentContextValue(addr.index,  line, column);
        } else {
            return new ContextValue(addr.upstairs, addr.index, line, column);
        }
    }
    
    private Expression createContextValueAtUpstair(int upstair, String name, int line, int column) {
        return createContextValue(varmgr.locateAtUpstair(name, upstair, line, column), line, column);
    }
    
    private Expression createContextValue(int upstair, String name, int line, int column) {
        return createContextValue(varmgr.locate(name, upstair, this.locateVarForce, line, column), line, column);
    }

    private CommonMethodDeclareExpression popNativeNewArrayDeclare(Class componentType, int line, int column) {
        Class classWaitCheck = componentType;
        while (classWaitCheck.isArray()) {
            classWaitCheck = classWaitCheck.getComponentType();
        }

        if (classWaitCheck == Void.class || classWaitCheck == Void.TYPE) {
            throw new ParseException("ComponentType must not Void.class", line, column);
        }

        final String path;
        if (engine.checkNativeAccess(path = (classWaitCheck.getName().concat(".[]"))) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        return new CommonMethodDeclareExpression(new NativeNewArrayDeclare(componentType), line, column);
    }

    private CommonMethodDeclareExpression popNativeMethodDeclare(Class clazz, String methodName, ClassNameList list, int line, int column) {

        final String path;
        if (engine.checkNativeAccess(path = (StringUtil.concat(clazz.getName(), ".", methodName))) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        try {
            final Method method = ClassUtil.searchMethod(clazz, methodName, list.toArray(), false);
            AsmMethodCaller caller;
            if (engine.isEnableAsmNative()) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(method)) {
                        try {
                            if ((caller = AsmMethodCallerManager.getCaller(method)) == null) {
                                logger.error(StringUtil.concat("AsmMethodCaller for '", method.toString(), "' is null, and instead by NativeMethodDeclare"));
                            }
                        } catch (Exception ex) {
                            caller = null;
                            logger.error(StringUtil.concat("Generate AsmMethodCaller for '", method.toString(), "' failed, and instead by NativeMethodDeclare"), ex);
                        }
                    } else {
                        logger.warn(StringUtil.concat("'", method.toString(), "' will not use asm, since this method is not public, and instead by NativeMethodDeclare"));
                        caller = null;
                    }
                } else {
                    logger.warn(StringUtil.concat("'", method.toString(), "' will not use asm, since class is not public, and instead by NativeMethodDeclare"));
                    caller = null;
                }
            } else {
                caller = null;
            }

            return new CommonMethodDeclareExpression(caller != null
                    ? new AsmNativeMethodDeclare(caller)
                    : new NativeMethodDeclare(method),
                    line, column);

        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }

    @SuppressWarnings("unchecked")
    private CommonMethodDeclareExpression popNativeConstructorDeclare(Class clazz, ClassNameList list, int line, int column) {

        final String path;
        if (engine.checkNativeAccess(path = (clazz.getName() + ".<init>")) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        try {
            final Constructor constructor = clazz.getConstructor(list.toArray());
            AsmMethodCaller caller;
            if (engine.isEnableAsmNative()) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(constructor)) {
                        try {
                            if ((caller = AsmMethodCallerManager.getCaller(constructor)) == null) {
                                logger.error(StringUtil.concat("AsmMethodCaller for '", constructor.toString(), "' is null, and instead by NativeConstructorDeclare"));
                            }
                        } catch (Exception ex) {
                            caller = null;
                            logger.error(StringUtil.concat("Generate AsmMethodCaller for '", constructor.toString(), "' failed, and instead by NativeConstructorDeclare"), ex);
                        }
                    } else {
                        logger.warn(StringUtil.concat("'", constructor.toString(), "' will not use asm, since this method is not public, and instead by NativeConstructorDeclare"));
                        caller = null;
                    }
                } else {
                    logger.warn(StringUtil.concat("'" + constructor.toString() + "' will not use asm, since class is not public, and instead by NativeConstructorDeclare"));
                    caller = null;
                }
            } else {
                caller = null;
            }

            return new CommonMethodDeclareExpression(caller != null
                    ? new AsmNativeMethodDeclare(caller)
                    : new NativeConstructorDeclare(constructor),
                    line, column);

        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }

    private static ResetableValueExpression castToResetableValueExpression(Expression expr) {
        if(expr instanceof ResetableValueExpression){
            return (ResetableValueExpression) expr;
        }else{
            throw new ParseException("Invalid expression to redirect out stream to, must be rewriteable", expr);
        }
    }

    private static Expression createSelfOperator(Expression lexpr, int sym, Expression rightExpr, int line, int column){
        ResetableValueExpression leftExpr = castToResetableValueExpression(lexpr);
        SelfOperator oper;
        switch (sym) {

            // (+ - * / %)=
            case Operators.PLUSEQ:
                oper = new SelfPlusOperator(leftExpr, rightExpr, line, column);
                break;
            case Operators.MINUSEQ:
                oper = new SelfMinusOperator(leftExpr, rightExpr, line, column);
                break;
            case Operators.MULTEQ:
                oper = new SelfMultOperator(leftExpr, rightExpr, line, column);
                break;
            case Operators.DIVEQ:
                oper = new SelfDivOperator(leftExpr, rightExpr, line, column);
                break;
            case Operators.MODEQ:
                oper = new SelfModOperator(leftExpr, rightExpr, line, column);
                break;

            // (<< >> >>>)=
            case Operators.LSHIFTEQ:
                oper = new SelfLShiftOperator(leftExpr, rightExpr, line, column);
                break;
            case Operators.RSHIFTEQ:
                oper = new SelfRShiftOperator(leftExpr, rightExpr, line, column);
                break;
            case Operators.URSHIFTEQ:
                oper = new SelfURShiftOperator(leftExpr, rightExpr, line, column);
                break;

            // (& ^ |)=
            case Operators.ANDEQ:
                oper = new SelfBitAndOperator(leftExpr, rightExpr, line, column);
                break;
            case Operators.XOREQ:
                oper = new SelfBitXorOperator(leftExpr, rightExpr, line, column);
                break;
            case Operators.OREQ:
                oper = new SelfBitOrOperator(leftExpr, rightExpr, line, column);
                break;

            default:
                throw new ParseException("Unsupported Operator", line, column);
        }

        return StatmentUtil.optimize(oper);
    }

    private static Expression createBinaryOperator(Expression leftExpr, int sym, Expression rightExpr, int line, int column) {

        BinaryOperator oper;
        switch (sym) {
            case Tokens.ANDAND: // &&
                oper = new AndOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.AND: // &
                oper = new BitAndOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.OR: // |
                oper = new BitOrOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.XOR: // ^
                oper = new BitXorOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.DIV: // /
                oper = new DivOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.EQEQ: // ==
                oper = new EqualsOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.GTEQ: // >=
                oper = new GreaterEqualsOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.GT: // >
                oper = new GreaterOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.LSHIFT: // <<
                oper = new LShiftOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.LTEQ: // <=
                oper = new LessEqualsOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.LT: // <
                oper = new LessOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.MINUS: // -
                oper = new MinusOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.MOD: // %
                oper = new ModOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.MULT: // *
                oper = new MultOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.NOTEQ: // !=
                oper = new NotEqualsOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.OROR: // ||
                oper = new OrOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.PLUS: // +
                oper = new PlusOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.RSHIFT: // >>
                oper = new RShiftOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.URSHIFT: // >>>
                oper = new URShiftOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.QUESTION_COLON: // ?:
                oper = new IfOrOperator(leftExpr, rightExpr, line, column);
                break;
            case Tokens.DOTDOT: // ..
                oper = new IntStepOperator(leftExpr, rightExpr, line, column);
                break;
            default:
                throw new ParseException("Unsupported Operator", line, column);
        }
        return StatmentUtil.optimize(oper);
    }


  final Object do_action(int actionId) throws ParseException {
      Stack<Symbol> myStack = this._stack;

      /* select the action based on the action number */
      switch (actionId){
	case 114: // contextValueIdent ::= FOR DOT IDENTIFIER 
	{
		 return ("for." + (String) myStack.peek(0).value).intern(); 
	}

	case 17: // statment ::= forInStatPart 
	{
		 return ((AbstractForInStatmentPart) myStack.peek(0).value).pop(); 
	}

	case 18: // statment ::= IDENTIFIER COLON forInStatPart 
	{
		 return ((AbstractForInStatmentPart) myStack.peek(0).value).pop(getLabelIndex((String) myStack.peek(2).value)); 
	}

	case 55: // forInStatPart ::= forInStatBody ELSE blockStat 
	{
		 return ((AbstractForInStatmentPart) myStack.peek(2).value).setElseStatment((IBlockStatment) myStack.peek(0).value); 
	}

	case 53: // forInStatBody ::= forInStatHead LBRACE statmentList RBRACE 
	{
		 return ((AbstractForInStatmentPart) myStack.peek(3).value).setStatmentList((StatmentList) myStack.peek(1).value); 
	}

	case 5: // classPureName ::= classPureName DOT IDENTIFIER 
	{
		 return ((ClassNameBand) myStack.peek(2).value).append((String) myStack.peek(0).value); 
	}

	case 7: // className ::= className LBRACK RBRACK 
	{
		 return ((ClassNameBand) myStack.peek(2).value).plusArrayDepth(); 
	}

	case 120: // expressionList1 ::= expressionList1 COMMA expression 
	{
		 return ((ExpressionList) myStack.peek(2).value).add((Expression) myStack.peek(0).value); 
	}

	case 102: // expression ::= funcHeadPrepare RPAREN LBRACE statmentList RBRACE 
	case 103: // expression ::= funcHead RPAREN LBRACE statmentList RBRACE 
	{
		 return ((FunctionPart) myStack.peek(4).value).pop(varmgr, (StatmentList) myStack.peek(1).value); 
	}

	case 48: // ifStat ::= ifStatPart 
	{
		 return ((IfStatmentPart) myStack.peek(0).value).pop(); 
	}

	case 49: // ifStat ::= ifStatPart ELSE blockStat 
	{
		 return ((IfStatmentPart) myStack.peek(2).value).pop((IBlockStatment) myStack.peek(0).value); 
	}

	case 50: // ifStat ::= ifStatPart ELSE ifStat 
	{
		 return ((IfStatmentPart) myStack.peek(2).value).pop((Statment) myStack.peek(0).value); 
	}

	case 30: // statment ::= importStatPart1 SEMICOLON 
	case 31: // statment ::= importStatPart2 SEMICOLON 
	{
		 return ((ImportStatmentPart) myStack.peek(1).value).pop(); 
	}

	case 41: // importStatPart2 ::= importStatPart1 contextValueExpr EQ IDENTIFIER 
	{
		 return ((ImportStatmentPart) myStack.peek(3).value).append((String) myStack.peek(0).value, (ResetableValueExpression) myStack.peek(2).value); 
	}

	case 43: // importStatPart2 ::= importStatPart2 COMMA contextValueExpr EQ IDENTIFIER 
	{
		 return ((ImportStatmentPart) myStack.peek(4).value).append((String) myStack.peek(0).value, (ResetableValueExpression) myStack.peek(2).value); 
	}

	case 124: // mapValuePart ::= mapValuePart COMMA DIRECT_VALUE COLON expression 
	{
		 return ((MapValuePart) myStack.peek(4).value).add((Object) myStack.peek(2).value, (Expression) myStack.peek(0).value); 
	}

	case 0: // templateAST ::= statmentList 
	{
		 return ((StatmentList) myStack.peek(0).value).popTemplateAST(varmgr.pop()); 
	}

	case 3: // statmentList ::= statmentList statment 
	{
		 return ((StatmentList) myStack.peek(1).value).add((Statment) myStack.peek(0).value); 
	}

	case 13: // statment ::= switchStatPart 
	{
		 return ((SwitchStatmentPart) myStack.peek(0).value).pop(); 
	}

	case 14: // statment ::= IDENTIFIER COLON switchStatPart 
	{
		 return ((SwitchStatmentPart) myStack.peek(0).value).pop(getLabelIndex((String) myStack.peek(2).value)); 
	}

	case 15: // statment ::= whileStatPart 
	{
		 return ((WhileStatmentPart) myStack.peek(0).value).pop(); 
	}

	case 16: // statment ::= IDENTIFIER COLON whileStatPart 
	{
		 return ((WhileStatmentPart) myStack.peek(0).value).pop(getLabelIndex((String) myStack.peek(2).value)); 
	}

	case 54: // forInStatPart ::= forInStatBody 
	{
		 return (AbstractForInStatmentPart) myStack.peek(0).value; 
	}

	case 6: // className ::= classPureName 
	{
		 return (ClassNameBand) myStack.peek(0).value; 
	}

	case 135: // classNameList ::= classNameList1 
	{
		 return (ClassNameList) myStack.peek(0).value; 
	}

	case 71: // expression_statmentable ::= funcExecuteExpr 
	case 72: // expression ::= expression_statmentable 
	{
		 return (Expression) myStack.peek(0).value; 
	}

	case 8: // statment ::= expression_statmentable SEMICOLON 
	case 101: // expression ::= LPAREN expression RPAREN 
	{
		 return (Expression) myStack.peek(1).value; 
	}

	case 122: // expressionList ::= expressionList1 
	{
		 return (ExpressionList) myStack.peek(0).value; 
	}

	case 11: // statment ::= blockStat 
	{
		 return (IBlockStatment) myStack.peek(0).value; 
	}

	case 112: // superCount ::= superCount SUPER DOT 
	{
		 return (Integer) myStack.peek(2).value + 1; 
	}

	case 110: // expression ::= mapValue 
	{
		 return (MapValue) myStack.peek(0).value; 
	}

	case 109: // expression ::= contextValueExpr 
	{
		 return (ResetableValueExpression) myStack.peek(0).value; 
	}

	case 12: // statment ::= ifStat 
	{
		 return (Statment) myStack.peek(0).value; 
	}

	case 113: // contextValueIdent ::= IDENTIFIER 
	{
		 return (String) myStack.peek(0).value; 
	}

	case 111: // superCount ::= SUPER DOT 
	{
		 return 1; 
	}

	case 9: // statment ::= SEMICOLON 
	case 21: // statment ::= varStatmentPart SEMICOLON 
	{
		 return NoneStatment.getInstance(); 
	}

	case 4: // classPureName ::= IDENTIFIER 
	{
		 return new ClassNameBand((String) myStack.peek(0).value); 
	}

	case 134: // classNameList ::= 
	{
		 return new ClassNameList(this.nativeImportMgr); 
	}

	case 119: // expressionList1 ::= expression 
	{
		 return new ExpressionList().add((Expression) myStack.peek(0).value); 
	}

	case 121: // expressionList ::= 
	{
		 return new ExpressionList(); 
	}

	case 123: // mapValuePart ::= DIRECT_VALUE COLON expression 
	{
		 return new MapValuePart().add((Object) myStack.peek(2).value, (Expression) myStack.peek(0).value); 
	}

	case 2: // statmentList ::= 
	{
		 return new StatmentList(); 
	}

	case 59: // switchStatPart0 ::= 
	{
		 return new SwitchStatmentPart(); 
	}

	case 10: // statment ::= expression PLACE_HOLDER_END 
	{
		 return placeHolderStatmentFactory.creatPlaceHolderStatment((Expression) myStack.peek(1).value); 
	}

	case 44: // blockStatPrepare ::= 
	case 45: // blockStatPrepare2 ::= LBRACE 
	{
		 varmgr.push(); return null; 
	}

	case 1: // $START ::= templateAST EOF 
	{
		/* ACCEPT */
		this.goonParse = false;
		return myStack.peek(1).value;
	}

	case 130: // funcExecuteExpr ::= expression LPAREN expressionList RPAREN 
	{
		Symbol funcExpr$Symbol = myStack.peek(3);
		 return new FunctionExecuteExpression((Expression) funcExpr$Symbol.value, ((ExpressionList) myStack.peek(1).value).toArray(), funcExpr$Symbol.line, funcExpr$Symbol.column); 
	}

	case 131: // funcExecuteExpr ::= expression AT contextValueExpr LPAREN expressionList RPAREN 
	{
		Symbol funcExpr$Symbol = myStack.peek(3);
		 return new FunctionExecuteExpression((ResetableValueExpression) funcExpr$Symbol.value, ((ExpressionList) myStack.peek(1).value).addFirst((Expression) myStack.peek(5).value).toArray(), funcExpr$Symbol.line, funcExpr$Symbol.column); 
	}

	case 127: // funcHead ::= funcHeadPrepare IDENTIFIER 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return ((FunctionPart) myStack.peek(1).value).appendArgIndexs(varmgr.assignVariant((String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column)); 
	}

	case 128: // funcHead ::= funcHead COMMA IDENTIFIER 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return ((FunctionPart) myStack.peek(2).value).appendArgIndexs(varmgr.assignVariant((String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column)); 
	}

	case 40: // importStatPart2 ::= importStatPart1 IDENTIFIER 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return ((ImportStatmentPart) myStack.peek(1).value).append((String) ident$Symbol.value, createContextValue(0, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column)); 
	}

	case 42: // importStatPart2 ::= importStatPart2 COMMA IDENTIFIER 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return ((ImportStatmentPart) myStack.peek(2).value).append((String) ident$Symbol.value, createContextValue(0, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column)); 
	}

	case 117: // contextValueExpr ::= superCount contextValueIdent 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return createContextValue((Integer) myStack.peek(1).value, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column); 
	}

	case 115: // contextValueExpr ::= contextValueIdent 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return createContextValue(0, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column); 
	}

	case 118: // contextValueExpr ::= superCount THIS DOT contextValueIdent 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return createContextValueAtUpstair((Integer) myStack.peek(3).value, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column); 
	}

	case 116: // contextValueExpr ::= THIS DOT contextValueIdent 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 return createContextValueAtUpstair(0, (String) ident$Symbol.value, ident$Symbol.line, ident$Symbol.column); 
	}

	case 35: // varStatmentPart ::= VAR IDENTIFIER 
	case 36: // varStatmentPart ::= varStatmentPart COMMA IDENTIFIER 
	{
		Symbol ident$Symbol = myStack.peek(0);
		 varmgr.assignVariant((String) ident$Symbol.value,ident$Symbol.line,ident$Symbol.column); return null;
	}

	case 64: // expression_statmentable ::= VAR IDENTIFIER EQ expression 
	{
		Symbol ident$Symbol = myStack.peek(2);
		Symbol sym$Symbol = myStack.peek(1);
		 return new AssignOperator(castToResetableValueExpression(createContextValue(varmgr.assignVariantAddress((String) ident$Symbol.value,ident$Symbol.line,ident$Symbol.column), ident$Symbol.line, ident$Symbol.column)), (Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 133: // classNameList1 ::= classNameList1 COMMA className 
	{
		Symbol nameBand$Symbol = myStack.peek(0);
		 return ((ClassNameList) myStack.peek(2).value).add((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column); 
	}

	case 132: // classNameList1 ::= className 
	{
		Symbol nameBand$Symbol = myStack.peek(0);
		 return new ClassNameList(this.nativeImportMgr).add((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column); 
	}

	case 58: // caseBlockStat ::= blockStatPrepare statmentList 
	{
		Symbol prepare$Symbol = myStack.peek(1);
		 return ((StatmentList) myStack.peek(0).value).popIBlockStatment(varmgr.pop(), prepare$Symbol.line, prepare$Symbol.column); 
	}

	case 46: // blockStat ::= blockStatPrepare2 statmentList RBRACE 
	{
		Symbol prepare$Symbol = myStack.peek(2);
		 return ((StatmentList) myStack.peek(1).value).popIBlockStatment(varmgr.pop(), prepare$Symbol.line, prepare$Symbol.column); 
	}

	case 100: // expression ::= DIRECT_VALUE 
	{
		Symbol sym$Symbol = myStack.peek(0);
		 return new DirectValue((Object) sym$Symbol.value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 68: // expression_statmentable ::= expression MINUSMINUS 
	{
		Symbol sym$Symbol = myStack.peek(0);
		 return new MinusMinusAfterOperator(castToResetableValueExpression((Expression) myStack.peek(1).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 66: // expression_statmentable ::= expression PLUSPLUS 
	{
		Symbol sym$Symbol = myStack.peek(0);
		 return new PlusPlusAfterOperator(castToResetableValueExpression((Expression) myStack.peek(1).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 20: // statment ::= TEXT_STATMENT 
	{
		Symbol sym$Symbol = myStack.peek(0);
		 return textStatmentFactory.getTextStatment(template, (char[]) sym$Symbol.value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 73: // expression ::= COMP expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return StatmentUtil.optimize(new BitNotOperator((Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column)); 
	}

	case 74: // expression ::= MINUS expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return StatmentUtil.optimize(new NegativeOperator((Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column)); 
	}

	case 75: // expression ::= NOT expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return StatmentUtil.optimize(new NotOperator((Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column)); 
	}

	case 78: // expression ::= expression MULT expression 
	case 79: // expression ::= expression DIV expression 
	case 80: // expression ::= expression MOD expression 
	case 81: // expression ::= expression PLUS expression 
	case 82: // expression ::= expression MINUS expression 
	case 83: // expression ::= expression LSHIFT expression 
	case 84: // expression ::= expression RSHIFT expression 
	case 85: // expression ::= expression URSHIFT expression 
	case 86: // expression ::= expression LT expression 
	case 87: // expression ::= expression LTEQ expression 
	case 88: // expression ::= expression GT expression 
	case 89: // expression ::= expression GTEQ expression 
	case 90: // expression ::= expression EQEQ expression 
	case 91: // expression ::= expression NOTEQ expression 
	case 92: // expression ::= expression AND expression 
	case 93: // expression ::= expression OR expression 
	case 94: // expression ::= expression XOR expression 
	case 95: // expression ::= expression ANDAND expression 
	case 96: // expression ::= expression DOTDOT expression 
	case 97: // expression ::= expression OROR expression 
	case 98: // expression ::= expression QUESTION_COLON expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return createBinaryOperator((Expression) myStack.peek(2).value, (Integer) sym$Symbol.value, (Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 69: // expression_statmentable ::= expression SELFEQ expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return createSelfOperator((Expression) myStack.peek(2).value, (Integer) sym$Symbol.value, (Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 63: // expression_statmentable ::= expression EQ expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new AssignOperator(castToResetableValueExpression((Expression) myStack.peek(2).value), (Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 24: // statment ::= BREAK SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new BreakStatment(0, sym$Symbol.line, sym$Symbol.column); 
	}

	case 26: // statment ::= CONTINUE SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new ContinueStatment(0, sym$Symbol.line, sym$Symbol.column); 
	}

	case 37: // importStatPart1 ::= IMPORT expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new ImportStatmentPart((Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 126: // mapValue ::= LBRACE RBRACE 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new MapValue(new Object[0], new Expression[0], sym$Symbol.line,sym$Symbol.column); 
	}

	case 67: // expression_statmentable ::= MINUSMINUS expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new MinusMinusBeforeOperator(castToResetableValueExpression((Expression) myStack.peek(0).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 65: // expression_statmentable ::= PLUSPLUS expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new PlusPlusBeforeOperator(castToResetableValueExpression((Expression) myStack.peek(0).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 76: // expression ::= expression DOT IDENTIFIER 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new PropertyOperator((Expression) myStack.peek(2).value, (String) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 70: // expression_statmentable ::= funcExecuteExpr EQGT expression 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new RedirectOutExpression((Expression) myStack.peek(2).value, castToResetableValueExpression((Expression) myStack.peek(0).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 28: // statment ::= RETURN SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 return new ReturnStatment(null, sym$Symbol.line, sym$Symbol.column); 
	}

	case 129: // funcHeadPrepare ::= FUNCTION LPAREN 
	{
		Symbol sym$Symbol = myStack.peek(1);
		 varmgr.push(); varmgr.pushVarWall(); return new FunctionPart(varmgr.assignVariant("arguments", sym$Symbol.line,sym$Symbol.column), sym$Symbol.line,sym$Symbol.column); 
	}

	case 23: // statment ::= NATIVE_IMPORT classPureName SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 nativeImportMgr.registClass((ClassNameBand) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); return NoneStatment.getInstance(); 
	}

	case 125: // mapValue ::= LBRACE mapValuePart RBRACE 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return ((MapValuePart) myStack.peek(1).value).pop(sym$Symbol.line, sym$Symbol.column); 
	}

	case 104: // expression ::= LBRACK expressionList RBRACK 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new ArrayValue(((ExpressionList) myStack.peek(1).value).toArray(), sym$Symbol.line, sym$Symbol.column); 
	}

	case 25: // statment ::= BREAK IDENTIFIER SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new BreakStatment(getLabelIndex((String) myStack.peek(1).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 27: // statment ::= CONTINUE IDENTIFIER SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new ContinueStatment(getLabelIndex((String) myStack.peek(1).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 22: // statment ::= ECHO expression SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new EchoStatment((Expression) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 38: // importStatPart1 ::= IMPORT expression mapValue 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new ImportStatmentPart((Expression) myStack.peek(1).value, (MapValue) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 32: // statment ::= INCLUDE expression SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new IncludeStatment((Expression) myStack.peek(1).value, null, sym$Symbol.line, sym$Symbol.column); 
	}

	case 77: // expression ::= expression LBRACK expression RBRACK 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new IndexOperator((Expression) myStack.peek(3).value, (Expression) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 19: // statment ::= blockStat EQGT expression SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new RedirectOutStatment((IBlockStatment) myStack.peek(3).value, castToResetableValueExpression((Expression) myStack.peek(1).value), sym$Symbol.line, sym$Symbol.column); 
	}

	case 29: // statment ::= RETURN expression SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(2);
		 return new ReturnStatment((Expression) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 61: // switchStatPart0 ::= DEFAULT COLON caseBlockStat switchStatPart0 
	{
		Symbol sym$Symbol = myStack.peek(3);
		 return ((SwitchStatmentPart) myStack.peek(0).value).appendCaseStatment(null, (IBlockStatment) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 99: // expression ::= expression QUESTION expression COLON expression 
	{
		Symbol sym$Symbol = myStack.peek(3);
		 return new IfOperator((Expression) myStack.peek(4).value, (Expression) myStack.peek(2).value, (Expression) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 33: // statment ::= INCLUDE expression mapValue SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(3);
		 return new IncludeStatment((Expression) myStack.peek(2).value, (MapValue) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 105: // expression ::= NATIVE LBRACK RBRACK className 
	{
		Symbol sym$Symbol = myStack.peek(3);
		Symbol nameBand$Symbol = myStack.peek(0);
		 return popNativeNewArrayDeclare(nativeImportMgr.toClass((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column), sym$Symbol.line, sym$Symbol.column); 
	}

	case 106: // expression ::= NATIVE LBRACK className RBRACK 
	{
		Symbol sym$Symbol = myStack.peek(3);
		Symbol nameBand$Symbol = myStack.peek(1);
		 return popNativeNewArrayDeclare(nativeImportMgr.toClass((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column), sym$Symbol.line, sym$Symbol.column); 
	}

	case 60: // switchStatPart0 ::= CASE DIRECT_VALUE COLON caseBlockStat switchStatPart0 
	{
		Symbol sym$Symbol = myStack.peek(4);
		 return ((SwitchStatmentPart) myStack.peek(0).value).appendCaseStatment((Object) myStack.peek(3).value, (IBlockStatment) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 47: // ifStatPart ::= IF LPAREN expression RPAREN blockStat 
	{
		Symbol sym$Symbol = myStack.peek(4);
		 return new IfStatmentPart((Expression) myStack.peek(2).value, (IBlockStatment) myStack.peek(0).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 39: // importStatPart1 ::= IMPORT expression LBRACE expression RBRACE 
	{
		Symbol sym$Symbol = myStack.peek(4);
		 return new ImportStatmentPart((Expression) myStack.peek(3).value, (Expression) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 56: // whileStatPart ::= WHILE LPAREN expression RPAREN blockStat 
	{
		Symbol sym$Symbol = myStack.peek(4);
		 return new WhileStatmentPart((Expression) myStack.peek(2).value, (IBlockStatment) myStack.peek(0).value, true, sym$Symbol.line, sym$Symbol.column); 
	}

	case 51: // forInStatHead ::= FOR LPAREN IDENTIFIER COLON expression RPAREN 
	{
		Symbol sym$Symbol = myStack.peek(5);
		 return new ForInStatmentPart((String) myStack.peek(3).value, (Expression) myStack.peek(1).value, this.varmgr, sym$Symbol.line, sym$Symbol.column); 
	}

	case 34: // statment ::= INCLUDE expression LBRACE expression RBRACE SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(5);
		 return new IncludeStatment((Expression) myStack.peek(4).value, (Expression) myStack.peek(2).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 108: // expression ::= NATIVE NEW classPureName LPAREN classNameList RPAREN 
	{
		Symbol sym$Symbol = myStack.peek(5);
		Symbol nameBand$Symbol = myStack.peek(3);
		 return popNativeConstructorDeclare(nativeImportMgr.toClass((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column), (ClassNameList) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 62: // switchStatPart ::= SWITCH LPAREN expression RPAREN LBRACE switchStatPart0 RBRACE 
	{
		Symbol sym$Symbol = myStack.peek(6);
		 return ((SwitchStatmentPart) myStack.peek(1).value).setSwitchExpr((Expression) myStack.peek(4).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 57: // whileStatPart ::= DO blockStat WHILE LPAREN expression RPAREN SEMICOLON 
	{
		Symbol sym$Symbol = myStack.peek(6);
		 return new WhileStatmentPart((Expression) myStack.peek(2).value, (IBlockStatment) myStack.peek(5).value, false, sym$Symbol.line, sym$Symbol.column); 
	}

	case 107: // expression ::= NATIVE classPureName DOT IDENTIFIER LPAREN classNameList RPAREN 
	{
		Symbol sym$Symbol = myStack.peek(6);
		Symbol nameBand$Symbol = myStack.peek(5);
		 return popNativeMethodDeclare(nativeImportMgr.toClass((ClassNameBand) nameBand$Symbol.value, nameBand$Symbol.line, nameBand$Symbol.column), (String) myStack.peek(3).value, (ClassNameList) myStack.peek(1).value, sym$Symbol.line, sym$Symbol.column); 
	}

	case 52: // forInStatHead ::= FOR LPAREN IDENTIFIER COMMA IDENTIFIER COLON expression RPAREN 
	{
		Symbol sym$Symbol = myStack.peek(7);
		 return new ForMapStatmentPart((String) myStack.peek(5).value, (String) myStack.peek(3).value, (Expression) myStack.peek(1).value, this.varmgr, sym$Symbol.line, sym$Symbol.column); 
	}

	default:
		throw new ParseException("Invalid action number found in internal parse table");

      }
  }

}
