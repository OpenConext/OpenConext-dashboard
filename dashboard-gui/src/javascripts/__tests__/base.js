import I18n from "i18n-js";
import en from "../locale/en";
import nl from "../locale/nl";
import pt from "../locale/pt";
import Adapter from "enzyme-adapter-react-16";
import Enzyme from "enzyme";

const start = () => {
  //we need to use them, otherwise the imports are deleted when organizing them
  expect(I18n).toBeDefined();
  expect(en).toBeDefined();
  expect(nl).toBeDefined();
  expect(pt).toBeDefined();
  I18n.locale = "en";

  Enzyme.configure({ adapter: new Adapter() });
};

test("Test suite must contain at least one test", () => {});

export default start;
