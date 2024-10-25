const {createProxyMiddleware} = require('http-proxy-middleware')

module.exports = function(app){
    const endpoint = process.env.REACT_PROXY_ENDPOINT || 'http://localhost:8080'
    app.use(
        '/api',
        createProxyMiddleware({
            target: endpoint,
            changeOrigin: false
        })
    )

    app.use(
        '/grafana',
        createProxyMiddleware(
            {
                target: 'http://localhost:3000',
                changeOrigin: false
            }
        )
    )
}