const proxy = require("http-proxy-middleware");
module.exports = function (app) {
    app.use(
        ["/login", "/startSSO", "/dashboard"],
        proxy({
            target: "http://localhost:8280",
            changeOrigin: true,
        })
    );
};