package nl.surfnet.coin.selfservice.service.impl;

import java.util.List;

import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.util.ConcurrentRunner;
import nl.surfnet.coin.selfservice.util.ConcurrentRunnerContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
  "classpath:coin-selfservice-context.xml",
  "classpath:coin-selfservice-with-real-cache.xml",
  "classpath:coin-selfservice-properties-context.xml",
  "classpath:coin-shared-context.xml"
})
@TransactionConfiguration(transactionManager = "selfServiceTransactionManager", defaultRollback = true)
@Transactional
public class CSPInitializationTest {

  @Autowired
  private CompoundSPService cspSvc;

  @Test
  public void test() {
    List<Integer> results = new ConcurrentRunnerContext<Integer>(20).run(new ConcurrentRunner() {
      @Override
      public Integer run() {
        List<CompoundServiceProvider> csps = cspSvc.getCSPsByIdp(new IdentityProvider("id", "institutionId", "name"));
        return csps.size();
      }
    });
    for (Integer oneResult : results) {
      assertEquals("all results of getCSPsByIdp should be the same", 57, oneResult.intValue());
    }}
}
