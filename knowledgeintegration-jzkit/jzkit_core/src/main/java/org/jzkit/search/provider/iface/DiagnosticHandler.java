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

/*
 * Title:       DiagnosticHandler
 * @version:    $Id: DiagnosticHandler.java,v 1.1.1.1 2004/06/18 06:38:18 ibbo Exp $
 * Copyright:   Copyright (C) 1999,2000 Knowledge Integration Ltd.
 * @author:     Rob Tice (rob.tice@k-int.com)
 * Company:     Knowledge Integration Ltd.
 * Description: This is the expected superclass for developers
 *   			who want to react specifically to diagnostic codes
 *   			This class simply logs the target and message for
 *   			every diagnostic code. Subclass and override the relevant
 *   			method to take specific action on a particular error.
 * 
 * Created 05-May-2003 16:10:49
 * $Log: DiagnosticHandler.java,v $
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
 * Revision 1.2  2003/05/09 14:12:32  rob_tice
 * *** empty log message ***
 *
 * Revision 1.1  2003/05/09 12:54:44  rob_tice
 * Updated diagnostics and started removal of unused imports
 *
 * 
 */
public class DiagnosticHandler
{
  public  void onPermanentSystemError(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTemporarySystemError(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedSearch(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTermsOnlyStopWords(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManyArgumentWords(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManyBooleanOperators(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManyTruncatedWords(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManyIncompleteSubfields(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTruncatedWordsTooShort(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onInvalidRecordNumberFormat(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManyCharsInSearchStmt(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManyRecordsRetrieved(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onPresentRequestOutOfRange(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onSysErrorPresentingRecords(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onRecUnauthorisedInterSystem(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onRecExceedsPreferredMessageSize(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onRecExceedsExceptionalSize(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onResSetUnsupportedAsSearchTerm(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onSingleResSetAsSearchTermOnly(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onSingleResSetAsSearchTermUseAndOnly(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onCantOverwriteExistingResSetName(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onResultSetNamingNotSupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onDatabaseCombinationUnsupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onElementSetNamesNotSupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onESNNotValidForSpecifiedDatabase(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onOnlyGenericESNSupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onResultSetAlreadyDeletedByTarget(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onResultSetInUse(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onOneOfSpecifiedDatabasesIsLocked(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onSpecifiedResultSetDoesNotExist(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onResourcesExhaustedNoResults(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onResourcesExhaustedUnpredictableResults(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onResourcesExhaustedValidSubsetAvailable(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onError(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onAccessControlFailure(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onChallengeRequiredOperationTerminated(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onChallengeRequiredRecNotSent(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onChallengeFailedRecNotSent(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTerminatedAtOriginRequest(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onNoAgreedSyntaxForRec(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onQueryTypeNotSupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onMalformedQuery(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onDatabaseUnavailable(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onOperatorUnsupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManyDatabasesSpecified(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManyResultSetsCreated(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedAttributeType(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedUseAttribute(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedTermValForUseAttr(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUseAttrRequiredButNotSupplied(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedRelationAttr(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedStructureAttr(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedPositionAttr(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedTruncationAttr(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedAttrSet(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedCompletenessAttr(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedAttrCombination(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedCodedValForTerm(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onMalformedSearchTerm(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onIllegalTermValforAttr(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnparsableFormatForUnNormalizedVal(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onIllegalResSetName(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onProximitySetSearchUnsupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};	
  public  void onIllegalResSetInProximitySearch(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedProximityRelation(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedProximityUnitCode(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onProximityUnsupportedWithSuppliedAttrs(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};	
  public  void onUnsupportedProximityDistance(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onProximityOrderedFlagNotSupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onScanZeroStepSizeOnly(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onScanSpecifedStepSizeNotSupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onCantSortAccordingToSequence(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onNoResSetNameSuppliedOnSort(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onDatabaseSpecificSortOnly(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onDatabaseSpecifcSortUnsupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManySortKeys(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onDuplicateSortKeys(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedMissingDataAction(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onIllegalSortRelation(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onIllegalCaseValue(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onIllegalMissingDataAction(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onRecsMightNotFitSpecifiedSegments(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onESPackageNameAlreadyInUse(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onESNoSuchPackageForModifyOrDelete(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onESQuotaExceeded(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onESTypeUnsupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onESPermissionDeniedUnauthorizedId(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onESPermissionDeniedCantModifyOrDelete(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onESImmediateExecutionFailed(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onESImmediateExecutionNotSupportedForService(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onESImmediateExecutionNotSupportedForParams(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onNoDataAvailableInRequestedSyntax(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onMalformedScan(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTermTypeNotSupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManyInputResultsForSort(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onIncompatibleRecFormatsForSort(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTermListUnsupportedForScan(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onUnsupportedValofPositionInResponseForScan(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onTooManyIndexTermsProcessed(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onDatabaseDoesntExist(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onAccessToSpecifiedDatabaseDenied(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onIllegalSort(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onRecNotAvailableInRequestedSyntax(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onRecSyntaxNotSupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onScanResourcesExhaustedNoSatisfyingTerms(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onStartOrEndOFTermListForScan(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onMaxSegmentSizeTooSmall(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onAdditionalRangesParamUnsupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onCompSpecParamUnsupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onRestrictionResultAttrOperandUnsupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onAttrValueComplexUnsupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onAttrSetinAttrElementUnsupported(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onIOExceptionSendingMessage(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onKintInvalidQuery(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onKintIOExceptionSendingMessage(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onKintSearchException(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onKintConnectionFailed(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onKintProblemParsingSortSpecification(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onKintSortFailure(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
  public  void onKintExternalCIDResponse(Diagnostic the_diagnostic){
    System.err.println("Target is "+the_diagnostic.target_name+" message is "+the_diagnostic.message);};
}
  
  
