package csa.model;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Statistics {
  private int totalQuestions = 0;
  private int totalLinkRequests = 0;
  private int totalUnlinkRequests = 0;
  private Map<String, Integer> institutionQuestions = new HashMap<String, Integer>();
  private Map<String, Integer> institutionLinkRequests = new HashMap<String, Integer>();
  private Map<String, Integer> institutionUnlinkRequests = new HashMap<String, Integer>();
  
  public Statistics() {
    //default constructor
  }

  public Statistics(final int totalQuestions, final int totalLinkRequests, final int totalUnlinkRequests) {
    this.totalQuestions = totalQuestions;
    this.totalLinkRequests = totalLinkRequests;
    this.totalUnlinkRequests = totalUnlinkRequests;
  }
  
  public int getTotalQuestions() {
    return totalQuestions;
  }
  
  public int getTotalLinkRequests() {
    return totalLinkRequests;
  }
  
  public int getTotalUnlinkRequests() {
    return totalUnlinkRequests;
  }
  
  public Map<String, Integer> getInstitutionQuestions() {
    return institutionQuestions;
  }
  
  public Map<String, Integer> getInstitutionLinkRequests() {
    return institutionLinkRequests;
  }
  
  public Map<String, Integer> getInstitutionUnlinkRequests() {
    return institutionUnlinkRequests;
  }
  
  public void countTotalQuestions() {
    totalQuestions += 1;
  }
  
  public void countTotalLinkRequests() {
    totalLinkRequests += 1;
  }

  public void countTotalUnlinkRequests() {
    totalUnlinkRequests += 1;
  }
  
  public void countInstitutionQuestion(final String institution) {
    addCounterInInstitutionMap(institutionQuestions, institution);
  }
  public void countInstitutionLinkRequest(final String institution) {
    addCounterInInstitutionMap(institutionLinkRequests, institution);
  }
  public void countInstitutionUnlinkRequest(final String institution) {
    addCounterInInstitutionMap(institutionUnlinkRequests, institution);
  }
  
  private void addCounterInInstitutionMap(Map<String, Integer> institutionMap, final String institution) {
    if (null == institutionMap.get(institution)) {
      institutionMap.put(institution, new Integer(1));
    } else {
      int count = institutionMap.get(institution);
      institutionMap.put(institution, ++count);
    }
  }
}
