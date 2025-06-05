const INVALID_EXTENSIONS = [".png", ".jpg", ".jpeg", ".gif", ".svg", ".ico"];

// Validate the URL
const isValidUrl = (url) => {
  if (!url) {
    console.log("URL is empty");
    return false;
  }

  if (!url.startsWith("http://") && !url.startsWith("https://")) {
    console.log("URL is not valid (http:// or https://)");
    return false;
  }

  if (INVALID_EXTENSIONS.some((extension) => url.endsWith(extension))) {
    console.log("URL is not valid (invalid extension)");
    return false;
  }
  return true;
};

module.exports = { isValidUrl };
