package dashboard.util;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

import dashboard.domain.LicenseContactPerson;

public class LicenseContactPersonService implements ApplicationListener<ContextRefreshedEvent> {

  private final Logger LOG = LoggerFactory.getLogger(LicenseContactPersonService.class);

  private final Resource resource;

  private Map<String, List<LicenseContactPerson>> persons = new HashMap<>();

  public LicenseContactPersonService(Resource resource) {
    this.resource = resource;
  }

  /*
   * 0 - Relatie
   * 1 - Relatienaam
   * 2 - Volledige naam (Eerste contactpersoon software)
   * 3 - E-mail (Eerste contactpersoon software)
   * 4 - Telefoonnummer 1 (Eerste contactpersoon software)
   * 5 - Categorie
   * 6 - IDP Naam
   * 7 - idp connection id
   * 8 - idp entity id
   */
  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    try {
      if (true) {
        return;
      }
      BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
      this.persons = br.lines().skip(1) //csv header
        .map(line -> line.split(","))
        .filter(columns -> columns.length > 8)
        .map(columns -> fromColumns(columns))
        .filter(LicenseContactPerson::isReachable)
        .collect(groupingBy(LicenseContactPerson::getIdpEntityId));
      LOG.info("Parsed {} license contact persons", persons.size());
    } catch (IOException e) {
      throw new RuntimeException("Unable to parse " + resource.getFilename(), e);
    }
  }

  private LicenseContactPerson fromColumns(String[] columns) {
    int correction = columns.length > 9 ? 1 : 0;
    String name = removeQuotes(correction == 1 ? String.format("%s %s", columns[3], columns[2]) : columns[2]);
    String email = removeQuotes(columns[3 + correction]);
    String phone = removeQuotes(columns[4 + correction]);
    String idpEntityId = removeQuotes(columns[7 + correction]);
    return new LicenseContactPerson(name, email, phone, idpEntityId);
  }

  private String removeQuotes(String s) {
    return s.replaceAll("\"", "").trim();
  }

  public List<LicenseContactPerson> licenseContactPersons(String idpEntityID) {
    return persons.getOrDefault(idpEntityID, new ArrayList<>());
  }

  public List<LicenseContactPerson> getPersons() {
    return persons.values().stream().flatMap(Collection::stream).collect(toList());
  }
}
