package nl.surfnet.coin.selfservice.control;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.csa.model.OfferedService;
import nl.surfnet.coin.csa.model.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OfferedServicePresenter {

  public static class OfferedServiceView {
    private final OfferedService offeredService;

    public OfferedServiceView(OfferedService offeredService) {
      this.offeredService = offeredService;
    }

    public OfferedService getOfferedService() {
      return offeredService;
    }

  }

  private Ordering<OfferedServiceView> byName = new Ordering<OfferedServiceView>() {
    @Override
    public int compare(OfferedServiceView left, OfferedServiceView right) {
      return left.getOfferedService().getService().getName().compareTo(right.getOfferedService().getService().getName());
    }
  };

  private final Collection<OfferedServiceView> offeredServiceViews;

  public OfferedServicePresenter(List<OfferedService> offeredServices) {
    offeredServiceViews = Collections2.transform(offeredServices, new Function<OfferedService, OfferedServiceView>() {
      @Override
      public OfferedServiceView apply(OfferedService input) {
        return new OfferedServiceView(input);
      }
    });
  }

  public Collection<OfferedServiceView> getOfferedServiceViews() {
    return byName.sortedCopy(offeredServiceViews);
  }
}
