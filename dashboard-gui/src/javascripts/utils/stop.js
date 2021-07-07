export default function stopEvent(e) {
  if (e) {
    e.preventDefault();
    e.stopPropagation();
  }
}