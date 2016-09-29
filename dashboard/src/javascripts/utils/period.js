export const getPeriod = (moment, type) => {
  switch (type) {
  case "y":
    return moment.year();
  case "q":
    return moment.year() + "q" + moment.quarter();
  case "m":
    return moment.year() + "m" + (moment.month() + 1);
  case "w":
    return moment.year() + "w" + moment.week();
  case "d":
    return moment.year() + "d" + moment.dayOfYear();
  default:
    return null;
  }
};
