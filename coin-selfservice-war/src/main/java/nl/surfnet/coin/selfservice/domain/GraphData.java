package nl.surfnet.coin.selfservice.domain;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.LocalDate;
import org.surfnet.cruncher.model.LoginData;

import java.util.*;

public class GraphData {
  private static final int MICROSECONDS_IN_SECOND = 1000;
  private static final String DATE_PATTERN = "dd-MM-yyyy";

  private String spName;
  private String idpName;
  private String spEntityId;
  private String idpEntityId;
  private long pointStart;
  private long pointEnd;
  private LinkedHashMap<Long, Integer> graphData;

  @JsonIgnore
  private String[] csvRow;
  @JsonIgnore
  private String[] csvHeaders;

  public static GraphData forLoginData(LoginData loginData) {
    LinkedHashMap<Long, Integer> data = new LinkedHashMap<>();

    List<String> csvHeaders = new ArrayList<>();
    csvHeaders.addAll(Arrays.asList("idpEntityId", "idpName", "spEntityId", "startDate", "endDate"));

    for (int i = 0; i < loginData.getData().size(); i++) {
      long timestamp = loginData.getPointStart() + i * loginData.getPointInterval();

      data.put(timestamp / MICROSECONDS_IN_SECOND, loginData.getData().get(i));
      csvHeaders.add(new LocalDate(timestamp).toString(DATE_PATTERN));
    }

    List<String> csvRow = new ArrayList<>();
    csvRow.add(loginData.getIdpEntityId());
    csvRow.add(loginData.getIdpname());
    csvRow.add(loginData.getSpEntityId());
    csvRow.add(new LocalDate(loginData.getPointStart()).toString(DATE_PATTERN));
    csvRow.add(new LocalDate(loginData.getPointEnd()).toString(DATE_PATTERN));

    csvRow.addAll(Collections2.transform(data.values(), new Function<Integer, String>() {
      @Override
      public String apply(Integer integer) {
        return integer == null ? "" : integer.toString();
      }
    }));

    return new GraphData(
      loginData.getSpName(),
      loginData.getIdpname(),
      loginData.getSpEntityId(),
      loginData.getIdpEntityId(),
      loginData.getPointStart(),
      loginData.getPointEnd(),
      data,
      csvRow.toArray(new String[csvRow.size()]),
      csvHeaders.toArray(new String[csvHeaders.size()])
    );
  }


  private GraphData(String spName, String idpName, String spEntityId, String idpEntityId, long pointStart, long pointEnd, LinkedHashMap<Long, Integer> graphData, String[] csvRow, String[] csvHeaders) {
    this.spName = spName;
    this.idpName = idpName;
    this.spEntityId = spEntityId;
    this.idpEntityId = idpEntityId;
    this.pointStart = pointStart;
    this.pointEnd = pointEnd;
    this.graphData = graphData;
    this.csvRow = csvRow;
    this.csvHeaders = csvHeaders;
  }

  public String getSpName() {
    return spName;
  }

  public String getIdpName() {
    return idpName;
  }

  public String getSpEntityId() {
    return spEntityId;
  }

  public String getIdpEntityId() {
    return idpEntityId;
  }

  public long getPointStart() {
    return pointStart;
  }

  public long getPointEnd() {
    return pointEnd;
  }

  public LinkedHashMap<Long, Integer> getGraphData() {
    return graphData;
  }

  public String[] getCsvRow() {
    return csvRow;
  }

  public String[] getCsvHeaders() {
    return csvHeaders;
  }

}
