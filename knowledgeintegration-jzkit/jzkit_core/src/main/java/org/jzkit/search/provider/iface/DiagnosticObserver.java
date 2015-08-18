/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * published by the Free Software Foundation; either version 2.1 of
 * the license, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite
 * 330, Boston, MA  02111-1307, USA.
 * 
 */
 
package org.jzkit.search.provider.iface;

import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

// For logging
/*
 * Title:       DiagnosticObserver
 * @version:    $Id: DiagnosticObserver.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Rob Tice (rob.tice@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: 
 * 
 * Created 05-May-2003 15:59:01
 * $Log: DiagnosticObserver.java,v $
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
 * Revision 1.3  2003/06/03 10:35:43  rob_tice
 * Now uses the diagnostic code string to decide diagnostic type
 * Has a new method additionalDiagnostics which can be overridden to deal with specific non bib1 or K-Int additional diagnostic codes if required
 *
 * Revision 1.2  2003/05/09 14:12:49  rob_tice
 * *** empty log message ***
 *
 * Revision 1.1  2003/05/09 12:54:44  rob_tice
 * Updated diagnostics and started removal of unused imports
 *
 * 
 */
public class DiagnosticObserver implements Observer
{
	protected DiagnosticHandler diagnostic_handler 	= null;
	protected Properties diagnostic_props 			= new Properties();
	
	/**
	 * 
	 * @param diagnostic_handler This is expected to be a subclass of 
	 * DiagnosticHandler which overrides the diagnostic methods it needs. 
	 * The methods are in the DiagnosticHandler class simply log at debug level.
	 * 
	 * e.g. DiagnosticHandler  my_handler  = new MyDiagnosticHandler();
	 * 		DiagnosticObserver my_observer = new DiagnosticObserver(my_handler);
	 * The messages for the diagnostics are loaded from the diagnostic.properties file.
	 */
	public DiagnosticObserver(DiagnosticHandler diagnostic_handler) //throws DiagnosticException
	{
		this.diagnostic_handler = diagnostic_handler;
		
		try
		{
			diagnostic_props.load(this.getClass().getResourceAsStream("/com/k_int/IR/diagnostics.properties")); 	
		}
		catch (Exception e)
		{
			// cat.warn("Unable to load diagnostic properties file");
		}			
	}
	
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		if ( ((IREvent)arg).event_type!=IREvent.DIAGNOSTIC_EVENT )
			return;
			
		DiagnosticEvent e 			= (DiagnosticEvent)arg;	
		Diagnostic the_diagnostic 	= (Diagnostic)e.event_info;

		if (the_diagnostic.status_code.toUpperCase().startsWith("DIAG.BIB1"))
			bib1Diagnostic(the_diagnostic);
		else if (the_diagnostic.status_code.toUpperCase().startsWith("DIAG.K-INT"))
			kIntDiagnostic(the_diagnostic);
		else
			additionalDiagnostics(the_diagnostic);
	}
	
	protected void bib1Diagnostic(Diagnostic the_diagnostic)
	{
		the_diagnostic.message 	= (String) diagnostic_props.get(the_diagnostic.status_code);		
		int int_status_code 	= the_diagnostic.getIntegerDiagnosticCode().intValue();
		
		switch(int_status_code)
		{
			 case Bib1Diagnostics.ACCESS_CONTROL_FAILURE:
				diagnostic_handler.onAccessControlFailure(the_diagnostic);
				break;
			 case Bib1Diagnostics.ACCESS_TO_SPECD_DBASE_DENIED:
				diagnostic_handler.onAccessToSpecifiedDatabaseDenied(the_diagnostic);
				break;
			case Bib1Diagnostics.ADDITIONAL_RANGES_PARM_UNSUPPORTED:
				diagnostic_handler.onAdditionalRangesParamUnsupported(the_diagnostic);
				break;	
			case Bib1Diagnostics.ATTR_SET_IN_ATTR_ELEMENT_UNSUPPORTED:
				diagnostic_handler.onAttrSetinAttrElementUnsupported(the_diagnostic);
				break;
			case Bib1Diagnostics.ATTR_VAL_complex_UNSUPPORTED:
				diagnostic_handler.onAttrValueComplexUnsupported(the_diagnostic);
				break;	
			case Bib1Diagnostics.CANT_OVERWRITE_EXISTING_RES_SET_NAME:
				diagnostic_handler.onCantOverwriteExistingResSetName(the_diagnostic);	
				break;
			case Bib1Diagnostics.CANT_SORT_ACCORDING_TO_SEQUENCE:
				diagnostic_handler.onCantSortAccordingToSequence(the_diagnostic);
				break;
			case Bib1Diagnostics.CHALLENGE_FAILED_REC_NOT_SENT:
				diagnostic_handler.onChallengeFailedRecNotSent(the_diagnostic);
				break;
			case Bib1Diagnostics.CHALLENGE_REQUIRED_OP_TERMINATED:
				diagnostic_handler.onChallengeRequiredOperationTerminated(the_diagnostic);
				break;
			case Bib1Diagnostics.CHALLENGE_REQUIRED_REC_NOT_SENT:
				diagnostic_handler.onChallengeRequiredRecNotSent(the_diagnostic);
				break;
			case Bib1Diagnostics.COMP_SPEC_PARAM_UNSUPPORTED:
				diagnostic_handler.onCompSpecParamUnsupported(the_diagnostic);
				break;
			case Bib1Diagnostics.DBASE_COMBINATION_UNSUPPORTED:
				diagnostic_handler.onDatabaseCombinationUnsupported(the_diagnostic);
				break;
			case Bib1Diagnostics.DBASE_DOESNT_EXIST:
				diagnostic_handler.onDatabaseDoesntExist(the_diagnostic);
				break;
			case Bib1Diagnostics.DBASE_SPECIFIC_SORT_UNSUPPORTED:
				diagnostic_handler.onDatabaseSpecifcSortUnsupported(the_diagnostic);
				break;
			case Bib1Diagnostics.DBASE_SPECIFIC_SORT_ONLY:
				diagnostic_handler.onDatabaseSpecificSortOnly(the_diagnostic);
				break;
			case Bib1Diagnostics.DBASE_UNAVALABLE:
				diagnostic_handler.onDatabaseUnavailable(the_diagnostic);
				break;
			case Bib1Diagnostics.DUPLICATE_SORT_KEYS:
				diagnostic_handler.onDuplicateSortKeys(the_diagnostic);
				break;
			case Bib1Diagnostics.ELEMENT_SET_NAMES_UNSUPPORTED:
				diagnostic_handler.onElementSetNamesNotSupported(the_diagnostic);
				break;
			case Bib1Diagnostics.ERROR :
				diagnostic_handler.onError(the_diagnostic);
				break;
			case Bib1Diagnostics.ES_IMMEDIATE_EXECUTE_FAILED :
				diagnostic_handler.onESImmediateExecutionFailed(the_diagnostic);
				break;
			case Bib1Diagnostics.ES_NO_IMMEDIATE_EXEC_FOR_THESE_PARMS :
				diagnostic_handler.onESImmediateExecutionNotSupportedForParams(the_diagnostic);
				break;
			case Bib1Diagnostics.ES_NO_IMMEDIATE_EXEC_FOR_THIS_SERVCE :
				diagnostic_handler.onESImmediateExecutionNotSupportedForService(the_diagnostic);
				break;
			case Bib1Diagnostics.ES_NO_SUCH_PACKAGE_ON_MOD_OR_DEL:
				diagnostic_handler.onESNoSuchPackageForModifyOrDelete(the_diagnostic);
				break;
			case Bib1Diagnostics.ES_PACKAGE_NAME_ALREADY_IN_USE :
				diagnostic_handler.onESPackageNameAlreadyInUse(the_diagnostic);
				break;
			case Bib1Diagnostics.ES_PERMISSION_DENIED_CANT_MOD_OR_DEL :
				diagnostic_handler.onESPermissionDeniedCantModifyOrDelete(the_diagnostic);
				break;
			case Bib1Diagnostics.ES_PERMISSION_DENIED_ID_UNAUTHORIZED :
				diagnostic_handler.onESPermissionDeniedUnauthorizedId(the_diagnostic);
				break;
			case Bib1Diagnostics.ES_QUOTA_EXCEEDED :
				diagnostic_handler.onESQuotaExceeded(the_diagnostic);
				break;
			case Bib1Diagnostics.ES_TYPE_UNSUPPORTED :
				diagnostic_handler.onESTypeUnsupported(the_diagnostic);
				break;
			case Bib1Diagnostics.ESN_INVALID_FOR_SPECIFIED_DBASE:
				diagnostic_handler.onESNNotValidForSpecifiedDatabase(the_diagnostic);
				break;
			case Bib1Diagnostics.ILLEGAL_CASE_VALUE :
				diagnostic_handler.onIllegalCaseValue(the_diagnostic);
				break;
			case Bib1Diagnostics.ILLEGAL_MISSING_DATA_ACTION :
				diagnostic_handler.onIllegalMissingDataAction(the_diagnostic);
				break;
			case Bib1Diagnostics.ILLEGAL_RES_SET_IN_PROXIMITY_SRCH :
				diagnostic_handler.onIllegalResSetInProximitySearch(the_diagnostic);
				break;
			case Bib1Diagnostics.ILLEGAL_RES_SET_NAME :
				diagnostic_handler.onIllegalResSetName(the_diagnostic);
				break;
			case Bib1Diagnostics.ILLEGAL_SORT :
				diagnostic_handler.onIllegalSort(the_diagnostic);
				break;
			case Bib1Diagnostics.ILLEGAL_SORT_RELATION :
				diagnostic_handler.onIllegalSortRelation(the_diagnostic);
				break;
			case Bib1Diagnostics.ILLEGAL_TERM_VAL_FOR_ATTR :
				diagnostic_handler.onIllegalTermValforAttr(the_diagnostic);
				break;
			case Bib1Diagnostics.INCOMPATIBLE_REC_FORMATS_FOR_SORT :
				diagnostic_handler.onIncompatibleRecFormatsForSort(the_diagnostic);
				break;
			case Bib1Diagnostics.INVALID_REC_NUMBER_FORMAT:
				diagnostic_handler.onInvalidRecordNumberFormat(the_diagnostic);
				break;
			case Bib1Diagnostics.MALFORMED_QUERY :
				diagnostic_handler.onMalformedQuery(the_diagnostic);
				break;
			case Bib1Diagnostics.MALFORMED_SCAN :
				diagnostic_handler.onMalformedScan(the_diagnostic);
				break;
			case Bib1Diagnostics.MALFORMED_SEARCH_TERM :
				diagnostic_handler.onMalformedSearchTerm(the_diagnostic);
				break;
			case Bib1Diagnostics.MAX_SEGMNT_SIZE_TOO_SMALL_FOR_REC :
				diagnostic_handler.onMaxSegmentSizeTooSmall(the_diagnostic);
				break;
			case Bib1Diagnostics.NO_ABSTRACT_SYNTAXES_AGREED_FOR_REC :
				diagnostic_handler.onNoAgreedSyntaxForRec(the_diagnostic);
				break;
			case Bib1Diagnostics.NO_DATA_AVAIL_IN_REQUESTED_SYNTAX :
				diagnostic_handler.onNoDataAvailableInRequestedSyntax(the_diagnostic);
				break;
			case Bib1Diagnostics.NO_RES_SET_NAME_SUPPLIED_FOR_SORT :
				diagnostic_handler.onNoResSetNameSuppliedOnSort(the_diagnostic);
				break;
			case Bib1Diagnostics.ONE_OF_SPECD_DBASES_IS_LOCKED :
				diagnostic_handler.onOneOfSpecifiedDatabasesIsLocked(the_diagnostic);
				break;
			case Bib1Diagnostics.ONLY_GENERIC_ESN_FORM_SUPPORTED :
				diagnostic_handler.onOnlyGenericESNSupported(the_diagnostic);
				break;
			case Bib1Diagnostics.OPERATOR_UNSUPPORTED:
				diagnostic_handler.onOperatorUnsupported(the_diagnostic);
				break;
			case Bib1Diagnostics.PERM_SYS_ERROR :
				diagnostic_handler.onPermanentSystemError(the_diagnostic);
				break;
			case Bib1Diagnostics.PRESENT_REQUEST_OUT_OF_RANGE :
				diagnostic_handler.onPresentRequestOutOfRange(the_diagnostic);
				break;
			case Bib1Diagnostics.PROX_ATTR_COMBINATION_UNSUPPORTED :
				diagnostic_handler.onProximityUnsupportedWithSuppliedAttrs(the_diagnostic);
				break;
			case Bib1Diagnostics.PROXIMITY_SET_SEARCH_UNSUPPORTED:
				diagnostic_handler.onProximitySetSearchUnsupported(the_diagnostic);
				break;
			case Bib1Diagnostics.PROXIMITY_ORDERED_FLAG_UNSUPPORTED :
				diagnostic_handler.onProximityOrderedFlagNotSupported(the_diagnostic);
				break;
			case Bib1Diagnostics.QUERY_TYPE_NOT_SUPPORTED :
				diagnostic_handler.onQueryTypeNotSupported(the_diagnostic);
				break;
			case Bib1Diagnostics.REC_ABOVE_EXCEPTIONAL_REC_SIZE :
				diagnostic_handler.onRecExceedsExceptionalSize(the_diagnostic);
				break;
			case Bib1Diagnostics.REC_ABOVE_PREF_MESSAGE_SIZE :
				diagnostic_handler.onRecExceedsPreferredMessageSize(the_diagnostic);
				break;
			case Bib1Diagnostics.REC_NOT_AVAIL_IN_REQUESTED_SYNTAX:
				diagnostic_handler.onRecNotAvailableInRequestedSyntax(the_diagnostic);
				break;
			case Bib1Diagnostics.REC_SYNTAX_NOT_SUPPORTED :
				diagnostic_handler.onRecSyntaxNotSupported(the_diagnostic);
				break;
			case Bib1Diagnostics.REC_UNATHORIZED_INTER_SYSTEM :
				diagnostic_handler.onRecUnauthorisedInterSystem(the_diagnostic);
				break;
			case Bib1Diagnostics.RECS_MIGHT_NOT_FIT_SPECD_SEGMENTS :
				diagnostic_handler.onRecsMightNotFitSpecifiedSegments(the_diagnostic);
				break;
			case Bib1Diagnostics.RES_SET_UNSUPPORTED_AS_SEARCH_TERM :
				diagnostic_handler.onResSetUnsupportedAsSearchTerm(the_diagnostic);
				break;
			case Bib1Diagnostics.RESOURCES_EXHAUSTED_ERRATIC_RESULTS :
				diagnostic_handler.onResourcesExhaustedUnpredictableResults(the_diagnostic);
				break;
			case Bib1Diagnostics.RESOURCES_EXHAUSTED_NO_RESULTS :
				diagnostic_handler.onResourcesExhaustedNoResults(the_diagnostic);
				break;
			case Bib1Diagnostics.RESOURCES_EXHAUSTED_VALID_RES_SUBSET :
				diagnostic_handler.onResourcesExhaustedValidSubsetAvailable(the_diagnostic);
				break;
			case Bib1Diagnostics.RESTRICTION_resultAttr_UNSUPPORTED :
				diagnostic_handler.onRestrictionResultAttrOperandUnsupported(the_diagnostic);
				break;
			case Bib1Diagnostics.RESULT_SET_ALREADY_DELETED_BY_TARGET :
				diagnostic_handler.onResultSetAlreadyDeletedByTarget(the_diagnostic);
				break;
			case Bib1Diagnostics.RESULT_SET_IN_USE :
				diagnostic_handler.onResultSetInUse(the_diagnostic);
				break;
			case Bib1Diagnostics.RESULT_SET_NAMING_UNSUPPORTED :
				diagnostic_handler.onResultSetNamingNotSupported(the_diagnostic);
				break;
			case Bib1Diagnostics.SCAN_RESOURCES_EXHAUSTED_NO_TERMS:
				diagnostic_handler.onScanResourcesExhaustedNoSatisfyingTerms(the_diagnostic);
				break;
			case Bib1Diagnostics.SINGLE_RES_SET_SRCH_TERM_ANDing_ONLY:
				diagnostic_handler.onSingleResSetAsSearchTermUseAndOnly(the_diagnostic);
				break;
			case Bib1Diagnostics.SINGLE_RES_SET_SRCH_TERM_ONLY:
				diagnostic_handler.onSingleResSetAsSearchTermOnly(the_diagnostic);
				break;
			case Bib1Diagnostics.SPECD_RESULT_SET_DOESNT_EXIST:
				diagnostic_handler.onSpecifiedResultSetDoesNotExist(the_diagnostic);
				break;
			case Bib1Diagnostics.SCAN_SPECD_STEP_SIZE_UNSUPPORTED :
				diagnostic_handler.onScanSpecifedStepSizeNotSupported(the_diagnostic);
				break;
			case Bib1Diagnostics.START_OR_END_OF_TERM_LIST_FOR_SCAN :
				diagnostic_handler.onStartOrEndOFTermListForScan(the_diagnostic);
				break;
			case Bib1Diagnostics.SYS_ERROR_PRESENTING_RECS:
				diagnostic_handler.onSysErrorPresentingRecords(the_diagnostic);
				break;
			case Bib1Diagnostics.SCAN_ZERO_STEP_SIZE_ONLY :
				diagnostic_handler.onScanZeroStepSizeOnly(the_diagnostic);
				break;
			case Bib1Diagnostics.TEMP_SYS_ERROR :
				diagnostic_handler.onTemporarySystemError(the_diagnostic);
				break;
			case Bib1Diagnostics.TERM_LIST_UNSUPPORTED_FOR_SCAN :
				diagnostic_handler.onTermListUnsupportedForScan(the_diagnostic);
				break;
			case Bib1Diagnostics.TERM_TYPE_UNSUPPORTED :
				diagnostic_handler.onTermTypeNotSupported(the_diagnostic);
				break;
			case Bib1Diagnostics.TERMINATED_AT_ORIGIN_REQUEST :
				diagnostic_handler.onTerminatedAtOriginRequest(the_diagnostic);
				break;
			case Bib1Diagnostics.TERMS_ONLY_STOP_WORDS :
				diagnostic_handler.onTermsOnlyStopWords(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_ARG_WORDS :
				diagnostic_handler.onTooManyArgumentWords(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_BOOLEAN_OPS :
				diagnostic_handler.onTooManyBooleanOperators(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_CHARS_IN_SRCH_STMT :
				diagnostic_handler.onTooManyCharsInSearchStmt(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_DBASES_SPECIFIED :
				diagnostic_handler.onTooManyDatabasesSpecified(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_INDEX_TERMS_PROCESSED:
				diagnostic_handler.onTooManyIndexTermsProcessed(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_INPUT_RESULTS_FOR_SORT :
				diagnostic_handler.onTooManyInputResultsForSort(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_RECS_RETRIEVED :
				diagnostic_handler.onTooManyRecordsRetrieved(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_RES_SETS_CREATED :
				diagnostic_handler.onTooManyResultSetsCreated(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_SORT_KEYS :
				diagnostic_handler.onTooManySortKeys(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_INCOMPLETE_SUB_FIELDS :
				diagnostic_handler.onTooManyIncompleteSubfields(the_diagnostic);
				break;
			case Bib1Diagnostics.TOO_MANY_TRUNCATED_WORDS :
				diagnostic_handler.onTooManyTruncatedWords(the_diagnostic);
				break;
			case Bib1Diagnostics.TRUNCATED_WORDS_TOO_SHORT :
				diagnostic_handler.onTruncatedWordsTooShort(the_diagnostic);
				break;
			case Bib1Diagnostics.UNPARSABLE_FMT_FOR_UN_NORMALIZED_VAL :
				diagnostic_handler.onUnparsableFormatForUnNormalizedVal(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_ATTR_COMNINATION :
				diagnostic_handler.onUnsupportedAttrCombination(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_ATTR_SET :
				diagnostic_handler.onUnsupportedAttrSet(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_ATTR_TYPE :
				diagnostic_handler.onUnsupportedAttributeType(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_CODED_VAL_FOR_TERM :
				diagnostic_handler.onUnsupportedCodedValForTerm(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_COMPLETENESS_ATTR :
				diagnostic_handler.onUnsupportedCompletenessAttr(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_MISSING_DATA_ACTION :
				diagnostic_handler.onUnsupportedMissingDataAction(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_POSITION_ATTR :
				diagnostic_handler.onUnsupportedPositionAttr(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_PROXIMITY_DISTANCE :
				diagnostic_handler.onUnsupportedProximityDistance(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_PROXIMITY_RELATION :
				diagnostic_handler.onUnsupportedProximityRelation(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_PROXIMITY_UNIT_CODE :
				diagnostic_handler.onUnsupportedProximityUnitCode(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_RELATION_ATTR:
				diagnostic_handler.onUnsupportedRelationAttr(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_SRCH :
				diagnostic_handler.onUnsupportedSearch(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_STRUCTURE_ATTR :
				diagnostic_handler.onUnsupportedStructureAttr(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_TERM_VAL_FOR_USE_ATTR :
				diagnostic_handler.onUnsupportedTermValForUseAttr(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_TRUNCATION_ATTR :
				diagnostic_handler.onUnsupportedTruncationAttr(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_USE_ATTR :
				diagnostic_handler.onUnsupportedUseAttribute(the_diagnostic);
				break;
			case Bib1Diagnostics.UNSUPPORTED_VAL_OF_POSN_IN_RESPONSE :
				diagnostic_handler.onUnsupportedValofPositionInResponseForScan(the_diagnostic);
				break;
			case Bib1Diagnostics.USE_ATTR_REQD_BUT_NOT_SUPPLIED :
				diagnostic_handler.onUseAttrRequiredButNotSupplied(the_diagnostic);
				break;					
			 default:
				// cat.warn("Unknown bib-1 diagnostic code "+the_diagnostic.status_code);
		}		
	}
	
	protected void kIntDiagnostic(Diagnostic the_diagnostic)
	{
		the_diagnostic.message 	= (String) diagnostic_props.get(the_diagnostic.status_code);
		int int_status_code 	= the_diagnostic.getIntegerDiagnosticCode().intValue();	
		switch(int_status_code)
		{
			 case KintDiagnostics.CONNECTION_FAILED:
				diagnostic_handler.onKintConnectionFailed(the_diagnostic);
				break;
			 case KintDiagnostics.INVALID_QUERY:
				diagnostic_handler.onKintInvalidQuery(the_diagnostic);
				break;
			case KintDiagnostics.IO_EXCEPTION_SENDING_MESSAGE:
				diagnostic_handler.onKintIOExceptionSendingMessage(the_diagnostic);
				break;	
			case KintDiagnostics.PROBLEM_PARSING_SORT_SPEC:
				diagnostic_handler.onKintProblemParsingSortSpecification(the_diagnostic);
				break;	
			case KintDiagnostics.SEARCH_EXCEPTION:
				diagnostic_handler.onKintSearchException(the_diagnostic);	
				break;
			case KintDiagnostics.SORT_FAILURE:
				diagnostic_handler.onKintSortFailure(the_diagnostic);
				break;
			case KintDiagnostics.EXTERNAL_CID_RESPONSE:
				diagnostic_handler.onKintExternalCIDResponse(the_diagnostic);
				break;
			default:
				// cat.warn("Unknown k-int diagnostic code "+the_diagnostic.status_code);
		}
	}

	protected void additionalDiagnostics(Diagnostic the_diagnostic)
	{
		// cat.warn("Undefined diagnostic "+the_diagnostic.status_code);		
	}
}
