// =====================================
// YOHARSHINI MART
// LOGIN + PRODUCT MANAGEMENT
// =====================================


// =====================================
// LOGIN
// =====================================

function login() {

    let email =
        document.getElementById("email").value;

    let password =
        document.getElementById("password").value;

    if (email === "admin@gmail.com" && password === "12345") {

        window.location.href = "home.html";

    } else {

        document.getElementById("message").innerHTML =
            "❌ Invalid Email or Password";
    }
}


// =====================================
// PRODUCTS
// =====================================

let products = JSON.parse(
    localStorage.getItem("yoharshiniProducts")
) || [];


// =====================================
// SHOW PAGE
// =====================================

function showPage(pageName) {

    let pages = document.querySelectorAll(".page");

    pages.forEach(function(page) {
        page.classList.add("hidden");
    });

    let selectedPage =
        document.getElementById(pageName);

    if (selectedPage) {
        selectedPage.classList.remove("hidden");
    }

    if (pageName === "products") {
        displayProducts();
    }

    updateTotalProducts();
}


// =====================================
// TOTAL PRODUCTS
// =====================================

function updateTotalProducts() {

    let total = 512 + products.length;

    let totalElement =
        document.getElementById("totalProducts");

    if (totalElement) {
        totalElement.innerText = total;
    }
}


// =====================================
// ADD PRODUCT
// =====================================

function addProduct() {

    let name =
        document.getElementById("productName").value.trim();

    let price =
        document.getElementById("productPrice").value;

    let category =
        document.getElementById("productCategory").value;

    let stock =
        document.getElementById("productStock").value;

    let imageInput =
        document.getElementById("productImage");


    if (name === "") {
        alert("Please enter product name");
        return;
    }

    if (price === "" || Number(price) <= 0) {
        alert("Please enter valid price");
        return;
    }

    if (category === "") {
        alert("Please select category");
        return;
    }

    if (stock === "" || Number(stock) < 0) {
        alert("Please enter valid stock");
        return;
    }

    if (imageInput.files.length === 0) {
        alert("Please select product image");
        return;
    }


    let reader = new FileReader();

    reader.onload = function(event) {

        let product = {

            id: Date.now(),

            name: name,

            price: Number(price),

            category: category,

            stock: Number(stock),

            image: event.target.result
        };


        products.push(product);


        localStorage.setItem(
            "yoharshiniProducts",
            JSON.stringify(products)
        );


        updateTotalProducts();


        document.getElementById("productName").value = "";
        document.getElementById("productPrice").value = "";
        document.getElementById("productCategory").value = "";
        document.getElementById("productStock").value = "";
        document.getElementById("productImage").value = "";


        alert("✅ Product added successfully!");


        showPage("products");
    };


    reader.readAsDataURL(imageInput.files[0]);
}


// =====================================
// DISPLAY PRODUCTS
// =====================================

function displayProducts() {

    let productList =
        document.getElementById("productList");

    if (!productList) {
        return;
    }


    productList.innerHTML = "";


    if (products.length === 0) {

        productList.innerHTML = `
            <div class="form-box">
                <h2>No Products Added</h2>
                <p>Click Add Product to add your first product.</p>
            </div>
        `;

        return;
    }


    products.forEach(function(product) {

        productList.innerHTML += `

            <div class="product">

                <img
                    src="${product.image}"
                    alt="${product.name}"
                >

                <div class="product-content">

                    <h3>${product.name}</h3>

                    <p>
                        <b>Price:</b> ₹${product.price}
                    </p>

                    <p>
                        <b>Category:</b>
                        ${product.category}
                    </p>

                    <p>
                        <b>Stock:</b>
                        ${product.stock}
                    </p>

                    <button
                        class="delete-btn"
                        onclick="deleteProduct(${product.id})"
                    >
                        🗑️ Delete
                    </button>

                </div>

            </div>

        `;
    });
}


// =====================================
// DELETE PRODUCT
// =====================================

function deleteProduct(id) {

    let answer =
        confirm("Do you want to delete this product?");

    if (!answer) {
        return;
    }


    products = products.filter(function(product) {

        return product.id !== id;

    });


    localStorage.setItem(
        "yoharshiniProducts",
        JSON.stringify(products)
    );


    updateTotalProducts();

    displayProducts();

    alert("🗑️ Product deleted successfully!");
}


// =====================================
// SEARCH
// =====================================

function searchProducts(value) {

    value = value.toLowerCase().trim();

    let productCards =
        document.querySelectorAll(".product");


    productCards.forEach(function(card) {

        let text =
            card.innerText.toLowerCase();

        if (text.includes(value)) {

            card.style.display = "block";

        } else {

            card.style.display = "none";
        }

    });
}


// =====================================
// PAGE LOAD
// =====================================

updateTotalProducts();

displayProducts();
