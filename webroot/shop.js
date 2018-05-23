
var cart = new Vue({
    el: '#cart',
    data: {
        timer_cookie: this.$cookies.get('timer'),
        default_buytext: 'Buy',
        default_description: 'Buy this book!',
        buytext: 'Buy',
        added_text: 'Buy a book!',
        in_time: 'false',
        discount: 0,
        products: []
    },
    computed: {
        price: {
            get: function() {
              var total = this.products.reduce(function(prev, product) { return prev + product.price }, 0);
              return total - this.discount;
            }
        }
    },
    watch: {
        products: function() {
            var timercookie = this.$cookies.get('timer');
            if (timercookie == "true") {
                this.timer();
            }
        }
    },
    methods: {
        buy: function() {
            axios.post('/buy', { action: "buy",
                products: this.products.map(function(product) { return product.id }),
                price: this.price })
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
            cart.discount = 10;

            var counter = 10;

            var countdown = setInterval(function() {
                cart.buytext = 'Buy now! ' + counter + ' seconds left';
                counter--;
                if (counter < 0) {
                    clearInterval(countdown);
                    cart.discount = 0;
                    cart.setCartTexts();
                }
            }, 1000);
        },
        setCartTexts: function() {
            cart.buytext = 'Buy';
            cart.added_text = 'Buy this book!';
        }
    }
});
