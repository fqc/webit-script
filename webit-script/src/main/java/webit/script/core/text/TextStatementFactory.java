// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.text;

import webit.script.Template;
import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public interface TextStatementFactory {

    void startTemplateParser(Template template);

    void finishTemplateParser(Template template);

    Statement getTextStatement(Template template, char[] text, int line, int column);
}
