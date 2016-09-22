const converters = {
  name: (value) => value.toLowerCase(),
  license: (value, app) => app.licenseStatus,
}

const converterForAttribute = (attr) => {
  return converters[attr];
};

const compare = function(a, b) {
  if (a < b) {
    return -1;
  } else if (a > b) {
    return 1;
  }
  return 0;
}

export default function sort(list, sortAttribute, sortAscending) {
  return list.sort((a, b) => {
    var aAttr = a[sortAttribute];
    var bAttr = b[sortAttribute];

    var converter = converterForAttribute(sortAttribute);

    if (converter) {
      aAttr = converter(aAttr, a);
      bAttr = converter(bAttr, b);
    }

    var result = compare(aAttr, bAttr);

    if (sortAscending) {
      return result * -1;
    }
    return result;
  });
};
