package nl.surfnet.coin.selfservice.control;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.surfnet.cruncher.model.LoginData;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LoginDataToCsv extends AbstractHttpMessageConverter<LoginDataResponse> {

  private static final String DATE_PATTERN = "dd-MM-yyyy";

  public final static class HeaderRow {

    private final List<LoginData> loginDatas;

    public HeaderRow(List<LoginData> loginDatas) {
      this.loginDatas = loginDatas;
    }

    public String[] toCsv() {
      List<String> headerRow = new ArrayList<>();
      headerRow.addAll(Arrays.asList("idpEntityId", "idpName", "spEntityId", "startDate", "endDate"));
      if(!loginDatas.isEmpty()) {

        LocalDate startDate = toLocalDate(loginDatas.get(0).getPointStart());
        LocalDate endDate = toLocalDate(loginDatas.get(0).getPointEnd());
        Days daysBetween = Days.daysBetween(startDate, endDate);
        headerRow.add(startDate.toString(DATE_PATTERN));
        for (int i = 0; i < daysBetween.getDays(); i++) {
          headerRow.add(startDate.plusDays(i + 1).toString(DATE_PATTERN));
        }
      }

      return headerRow.toArray(new String[headerRow.size()]);
    }

  }


  public static final MediaType MEDIA_TYPE = new MediaType("text", "csv", Charset.forName("utf-8"));

  public LoginDataToCsv() {
    super(MEDIA_TYPE);
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return LoginDataResponse.class.equals(clazz);
  }

  @Override
  protected LoginDataResponse readInternal(Class<? extends LoginDataResponse> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    throw new UnsupportedOperationException("not needed");
  }

  @Override
  protected void writeInternal(LoginDataResponse loginDataResponse, HttpOutputMessage output) throws IOException, HttpMessageNotWritableException {
    output.getHeaders().setContentType(MEDIA_TYPE);
    output.getHeaders().set("Content-Disposition", "attachment; filename=\"" + loginDataResponse.getFilename() + "\"");
    OutputStream out = output.getBody();
    try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {
      List<LoginData> loginDatas = loginDataResponse.getLoginData();
      List<String[]> csvRows = Lists.transform(loginDatas, new Function<LoginData, String[]>() {
        @Override
        public String[] apply(LoginData input) {
          List<String> result = new ArrayList<>();
          result.add(input.getIdpEntityId());
          result.add(input.getIdpname());
          result.add(input.getSpEntityId());
          result.add(toLocalDate(input.getPointStart()).toString(DATE_PATTERN));
          result.add(toLocalDate(input.getPointEnd()).toString(DATE_PATTERN));
          result.addAll(Lists.transform(input.getData(), new Function<Integer, String>() {
            public String apply(Integer input) {
              return input.toString();
            }
          }));
          return result.toArray(new String[result.size()]);
        }
      });
      HeaderRow headerRow = new HeaderRow(loginDatas);
      List<String[]> result = new ArrayList<>(csvRows);
      result.add(0, headerRow.toCsv());
      writer.writeAll(result);
    }

  }

  private static LocalDate toLocalDate(long pointStart) {
    return LocalDate.fromDateFields(new Date(pointStart));
  }
}
