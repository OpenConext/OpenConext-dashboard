// Hihi sneaky global

const spinner = {
  onStart: null,
  onStop: null,
  ignore: false,

  start: () => spinner.onStart && !spinner.ignore && spinner.onStart(),
  stop: () => spinner.onStop && !spinner.ignore && spinner.onStop(),
}

export default spinner
