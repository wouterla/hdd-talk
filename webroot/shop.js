
var cart = new Vue({
    el: '#cart',
    data: {
        timer_cookie: this.$cookies.get('timer'),
        default_buytext: 'Buy',
        default_description: 'Buy this book!',
        buytext: 'Buy',
        added_text: 'Buy a book!',
        in_time: 'false',
        products: []
    },
    computed: {
        price: {
            get: function() { return this.products.reduce(function(prev, product) { return prev + product.price }, 0) }
        }
    },
    watch: {
        products: function() {
            var timercookie = this.$cookies.get('timer');
            console.log("Timer: " + timercookie);
            if (timercookie == "true") {
                this.timer();
            }
        }
    },
    methods: {
        buy: function() {
            axios.post('/buy', { action: "buy", products: this.products.map(function(product) { return product.id }), with_timer: this.timer_cookie, in_time: this.in_time, price: this.price })
                .then(function (response) {
                    console.log(response);
                })
                .catch(function (error) {
                    console.log(error.message);
            });
        },
        timer: function() {
            cart.added_text = 'Buy within 10 seconds to get a $10,- discount!';
            cart.buytext = 'Buy with discount, 10 seconds left';

            cart.in_time = 'true';
            var counter = 10;

            var countdown = setInterval(function() {
                cart.buytext = 'Buy with discount, ' + counter + ' seconds left';
                counter--;
                if (counter < 0) {
                    clearInterval(countdown);
                    cart.reset();
                    cart.in_time = 'false';
                }
            }, 1000);
        },
        reset: function() {
            cart.buytext = 'Buy';
            cart.added_text = 'Buy this book!'
        }
    }
});


