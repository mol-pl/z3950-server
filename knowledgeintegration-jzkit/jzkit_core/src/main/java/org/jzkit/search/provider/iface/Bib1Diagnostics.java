package org.jzkit.search.provider.iface;

/*
 * Title:       Bib1Diagnostics
 * @version:    $Id: Bib1Diagnostics.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Rob Tice (rob.tice@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: A wrapper for the bib 1 diagnostic codes
 * 
 * Created 07-May-2003 18:04:17
 * $Log: Bib1Diagnostics.java,v $
 * Revision 1.1.1.1  2004/06/18 06:38:18  ibbo
 * Initial
 *
 * Revision 1.1.1.1  2004/03/08 15:25:38  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/12/05 16:30:44  ibbo
 * Initial Import
 *
 * Revision 1.1.1.1  2003/11/16 15:10:42  ibbo
 * Initial import
 *
 * Revision 1.1  2003/05/09 12:54:44  rob_tice
 * Updated diagnostics and started removal of unused imports
 *
 * 
 */
public class Bib1Diagnostics
{
	public static final int PERM_SYS_ERROR 						= 1;
	public static final int TEMP_SYS_ERROR 						= 2;
	public static final int UNSUPPORTED_SRCH					= 3;	
	public static final int TERMS_ONLY_STOP_WORDS				= 4;
	public static final int TOO_MANY_ARG_WORDS					= 5;
	public static final int TOO_MANY_BOOLEAN_OPS				= 6;
	public static final int TOO_MANY_TRUNCATED_WORDS			= 7;
	public static final int TOO_MANY_INCOMPLETE_SUB_FIELDS		= 8;
	public static final int TRUNCATED_WORDS_TOO_SHORT			= 9;
	public static final int INVALID_REC_NUMBER_FORMAT			= 10;
	public static final int TOO_MANY_CHARS_IN_SRCH_STMT			= 11;
	public static final int TOO_MANY_RECS_RETRIEVED				= 12;
	public static final int PRESENT_REQUEST_OUT_OF_RANGE		= 13;
	public static final int SYS_ERROR_PRESENTING_RECS			= 14;
	public static final int REC_UNATHORIZED_INTER_SYSTEM		= 15;
	public static final int REC_ABOVE_PREF_MESSAGE_SIZE			= 16;
	public static final int REC_ABOVE_EXCEPTIONAL_REC_SIZE		= 17;
	public static final int RES_SET_UNSUPPORTED_AS_SEARCH_TERM 	= 18;
	public static final int SINGLE_RES_SET_SRCH_TERM_ONLY		= 19;
	public static final int SINGLE_RES_SET_SRCH_TERM_ANDing_ONLY= 20;
	public static final int CANT_OVERWRITE_EXISTING_RES_SET_NAME= 21;
	public static final int RESULT_SET_NAMING_UNSUPPORTED		= 22;
	public static final int DBASE_COMBINATION_UNSUPPORTED 		= 23;
	public static final int ELEMENT_SET_NAMES_UNSUPPORTED		= 24;
	public static final int ESN_INVALID_FOR_SPECIFIED_DBASE		= 25;
	public static final int ONLY_GENERIC_ESN_FORM_SUPPORTED		= 26;
	public static final int RESULT_SET_ALREADY_DELETED_BY_TARGET= 27;
	public static final int RESULT_SET_IN_USE					= 28;
	public static final int ONE_OF_SPECD_DBASES_IS_LOCKED		= 29;
	public static final int SPECD_RESULT_SET_DOESNT_EXIST		= 30;
	public static final int RESOURCES_EXHAUSTED_NO_RESULTS		= 31;
	public static final int RESOURCES_EXHAUSTED_ERRATIC_RESULTS = 32;
	public static final int RESOURCES_EXHAUSTED_VALID_RES_SUBSET= 33;
	
	public static final int ERROR								= 100;
	public static final int ACCESS_CONTROL_FAILURE				= 101;
	public static final int CHALLENGE_REQUIRED_OP_TERMINATED 	= 102;
	public static final int CHALLENGE_REQUIRED_REC_NOT_SENT		= 103;
	public static final int CHALLENGE_FAILED_REC_NOT_SENT		= 104;
	public static final int TERMINATED_AT_ORIGIN_REQUEST		= 105;
	public static final int NO_ABSTRACT_SYNTAXES_AGREED_FOR_REC	= 106;
	public static final int QUERY_TYPE_NOT_SUPPORTED			= 107;
	public static final int MALFORMED_QUERY						= 108;
	public static final int DBASE_UNAVALABLE					= 109;
	public static final int OPERATOR_UNSUPPORTED				= 110;
	public static final int TOO_MANY_DBASES_SPECIFIED			= 111;
	public static final int TOO_MANY_RES_SETS_CREATED			= 112;
	public static final int UNSUPPORTED_ATTR_TYPE				= 113;
	public static final int UNSUPPORTED_USE_ATTR				= 114;
	public static final int UNSUPPORTED_TERM_VAL_FOR_USE_ATTR	= 115;
	public static final int USE_ATTR_REQD_BUT_NOT_SUPPLIED		= 116;
	public static final int UNSUPPORTED_RELATION_ATTR			= 117;
	public static final int UNSUPPORTED_STRUCTURE_ATTR			= 118;
	public static final int UNSUPPORTED_POSITION_ATTR			= 119;
	public static final int UNSUPPORTED_TRUNCATION_ATTR			= 120;
	public static final int UNSUPPORTED_ATTR_SET				= 121;
	public static final int UNSUPPORTED_COMPLETENESS_ATTR		= 122;
	public static final int UNSUPPORTED_ATTR_COMNINATION		= 123;
	public static final int UNSUPPORTED_CODED_VAL_FOR_TERM		= 124;
	public static final int MALFORMED_SEARCH_TERM				= 125;
	public static final int ILLEGAL_TERM_VAL_FOR_ATTR			= 126;
	public static final int UNPARSABLE_FMT_FOR_UN_NORMALIZED_VAL= 127;
	public static final int ILLEGAL_RES_SET_NAME				= 128;
	public static final int PROXIMITY_SET_SEARCH_UNSUPPORTED	= 129;
	public static final int ILLEGAL_RES_SET_IN_PROXIMITY_SRCH	= 130;
	public static final int UNSUPPORTED_PROXIMITY_RELATION		= 131;
	public static final int UNSUPPORTED_PROXIMITY_UNIT_CODE		= 132;
	
	public static final int PROX_ATTR_COMBINATION_UNSUPPORTED	= 201;	
	public static final int UNSUPPORTED_PROXIMITY_DISTANCE		= 202;
	public static final int PROXIMITY_ORDERED_FLAG_UNSUPPORTED	= 203;
	public static final int SCAN_ZERO_STEP_SIZE_ONLY			= 205;
	public static final int SCAN_SPECD_STEP_SIZE_UNSUPPORTED 	= 206;
	public static final int CANT_SORT_ACCORDING_TO_SEQUENCE		= 207;
	public static final int NO_RES_SET_NAME_SUPPLIED_FOR_SORT	= 208;
	public static final int DBASE_SPECIFIC_SORT_ONLY			= 209;
	public static final int DBASE_SPECIFIC_SORT_UNSUPPORTED		= 210;
	public static final int TOO_MANY_SORT_KEYS					= 211;
	public static final int DUPLICATE_SORT_KEYS					= 212;
	public static final int UNSUPPORTED_MISSING_DATA_ACTION		= 213;
	public static final int ILLEGAL_SORT_RELATION				= 214;
	public static final int ILLEGAL_CASE_VALUE					= 215;
	public static final int ILLEGAL_MISSING_DATA_ACTION			= 216;
	public static final int RECS_MIGHT_NOT_FIT_SPECD_SEGMENTS	= 217;
	public static final int ES_PACKAGE_NAME_ALREADY_IN_USE		= 218;
	public static final int ES_NO_SUCH_PACKAGE_ON_MOD_OR_DEL	= 219;
	public static final int ES_QUOTA_EXCEEDED					= 220;
	public static final int ES_TYPE_UNSUPPORTED					= 221;
	public static final int ES_PERMISSION_DENIED_ID_UNAUTHORIZED= 222;
	public static final int ES_PERMISSION_DENIED_CANT_MOD_OR_DEL= 223;
	public static final int ES_IMMEDIATE_EXECUTE_FAILED			= 224;
	public static final int ES_NO_IMMEDIATE_EXEC_FOR_THIS_SERVCE= 225;
	public static final int ES_NO_IMMEDIATE_EXEC_FOR_THESE_PARMS= 226;
	public static final int NO_DATA_AVAIL_IN_REQUESTED_SYNTAX	= 227;
	public static final int MALFORMED_SCAN						= 228;
	public static final int TERM_TYPE_UNSUPPORTED				= 229;
	public static final int TOO_MANY_INPUT_RESULTS_FOR_SORT		= 230;
	public static final int INCOMPATIBLE_REC_FORMATS_FOR_SORT	= 231;
	public static final int TERM_LIST_UNSUPPORTED_FOR_SCAN		= 232;
	public static final int UNSUPPORTED_VAL_OF_POSN_IN_RESPONSE	= 233;
	public static final int TOO_MANY_INDEX_TERMS_PROCESSED		= 234;
	public static final int DBASE_DOESNT_EXIST					= 235;
	public static final int ACCESS_TO_SPECD_DBASE_DENIED		= 236;
	public static final int ILLEGAL_SORT						= 237;
	public static final int REC_NOT_AVAIL_IN_REQUESTED_SYNTAX	= 238;
	public static final int REC_SYNTAX_NOT_SUPPORTED			= 239;
	public static final int SCAN_RESOURCES_EXHAUSTED_NO_TERMS	= 240;
	public static final int START_OR_END_OF_TERM_LIST_FOR_SCAN	= 241;
	public static final int MAX_SEGMNT_SIZE_TOO_SMALL_FOR_REC	= 242;
	public static final int ADDITIONAL_RANGES_PARM_UNSUPPORTED	= 243;
	public static final int COMP_SPEC_PARAM_UNSUPPORTED			= 244;
	public static final int RESTRICTION_resultAttr_UNSUPPORTED	= 245;
	public static final int ATTR_VAL_complex_UNSUPPORTED		= 246;
	public static final int ATTR_SET_IN_ATTR_ELEMENT_UNSUPPORTED= 247;
}
