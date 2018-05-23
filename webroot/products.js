var products = new Vue({
    el: '#products',
    data: {
        products: [
          {
              id: 1,
              title: "The Nature of Software Development",
              price: 50,
              img_url: "/covers/natureofsoftwaredev.jpeg",
              description: "Bringing Agile down to its basics -- me"
          },
          {
              id: 2,
              title: "Good Omens",
              price: 50,
              img_url: "/covers/goodomens.jpg",
              description: "Good Omens is a really good book! -- Adam"
          },
          {
              id: 3,
              title: "Humans vs Computers",
              price: 50,
              img_url: "/covers/humansvscomputers.jpg",
              description: "Humans vs Computers is scary and real -- me"
          }
        ],
        current: 0
    },
    computed: {
        currentProduct: function() {
            return this.products[this.current]
        }
    },
    methods: {
        addToCart: function() {
            cart.products.push(this.currentProduct);
        },
        next: function() {
            var next = this.current + 1;
            if (next > (this.products.length - 1)) {
                next = 0;
            }

            this.current = next;
        },
        previous: function() {
            if (this.current > 0) {
                this.current--;
            } else {
                this.current = this.products.length - 1;
            }
        }
    }

});
