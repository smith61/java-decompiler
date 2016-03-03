package net.jsmith.java.decomp.decompiler;

public class CSS {
	
	public static final String JAVA                = "java";
	
	public static final String JAVA_LINE           = JAVA + " java_line";

	public static final String JAVA_INDENT         = JAVA + " java_indent";
	
	public static final String JAVA_LABEL          = JAVA + " java_label";
	public static final String JAVA_COMMENT        = JAVA + " java_comment";
	
	public static final String JAVA_REF            = JAVA + " java_ref";
	public static final String JAVA_REF_TYPE       = JAVA_REF + " java_ref_type";
	public static final String JAVA_REF_FIELD      = JAVA_REF + " java_ref_field";
	public static final String JAVA_REF_METHOD     = JAVA_REF + " java_ref_method";
	public static final String JAVA_REF_PARAMETER  = JAVA_REF + " java_ref_parameter";
	public static final String JAVA_REF_VARIABLE   = JAVA_REF + " java_ref_variable";
	
	public static final String JAVA_DEF            = JAVA + " java_def";
	public static final String JAVA_DEF_TYPE       = JAVA_DEF + " java_def_type";
	public static final String JAVA_DEF_FIELD      = JAVA_DEF + " java_def_field";
	public static final String JAVA_DEF_METHOD     = JAVA_DEF + " java_def_method";
	public static final String JAVA_DEF_PARAMETER  = JAVA_DEF + " java_def_parameter";
	public static final String JAVA_DEF_VARIABLE   = JAVA_DEF + " java_def_variable";
	
	public static final String JAVA_LITERAL        = JAVA + " java_literal";
	public static final String JAVA_LITERAL_NUMBER = JAVA_LITERAL + " java_literal_number";
	public static final String JAVA_LITERAL_TEXT   = JAVA_LITERAL + " java_literal_text";
	
	public static final String JAVA_KEYWORD        = JAVA + " java_keyword";
	public static final String JAVA_KEYWORD( String keyword ) {
		return JAVA_KEYWORD + " java_keyword_" + keyword;
	}
	
}
