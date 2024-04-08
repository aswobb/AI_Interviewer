const gtmDataLayer = {
    install: function (Vue, options){
        const dataLayer = window.dataLayer || []
        Vue.prototype.$gtm = {
            sendCustomEvent: function(eventname){
                dataLayer.push({
                    event: eventname
                })
            },
            sendLoginEvent: function(username){
                dataLayer.push({
                    event: "log_in",
                    login_id: username
                })
            },
        }
    }
}

export default gtmDataLayer