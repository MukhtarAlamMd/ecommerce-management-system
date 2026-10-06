import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";

const CartPage = () => {
  const {
    cartItems,
    increaseQuantity,
    decreaseQuantity,
    removeFromCart,
    getCartTotal,
  } = useCart();

  const total = getCartTotal();

  if (cartItems.length === 0) {
    return (
      <div className="container mt-5">
        <div className="text-center">
          <h2 className="fw-bold">Your Cart</h2>

          <p className="text-muted mt-3">Your cart is empty.</p>

          <Link to="/products" className="btn btn-primary">
            Browse Products
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-5">
      <h2 className="fw-bold mb-4">Shopping Cart</h2>

      <div className="row">
        <div className="col-lg-8">
          {cartItems.map((item) => (
            <div className="card mb-3 shadow-sm" key={item.id}>
              <div className="card-body">
                <div className="row align-items-center">
                  <div className="col-md-5">
                    <h5>{item.name}</h5>

                    <p className="text-muted mb-1">{item.categoryName}</p>

                    <strong>₹{Number(item.price).toFixed(2)}</strong>
                  </div>

                  <div className="col-md-4">
                    <div className="d-flex align-items-center">
                      <button
                        className="btn btn-outline-secondary"
                        onClick={() => decreaseQuantity(item.id)}
                      >
                        −
                      </button>

                      <span className="mx-3 fw-bold">{item.quantity}</span>

                      <button
                        className="btn btn-outline-secondary"
                        onClick={() => increaseQuantity(item.id)}
                        disabled={item.quantity >= item.stockQuantity}
                      >
                        +
                      </button>
                    </div>

                    <small className="text-muted">
                      Stock: {item.stockQuantity}
                    </small>
                  </div>

                  <div className="col-md-3 text-end">
                    <p className="fw-bold">
                      ₹{(Number(item.price) * item.quantity).toFixed(2)}
                    </p>

                    <button
                      className="btn btn-sm btn-outline-danger"
                      onClick={() => removeFromCart(item.id)}
                    >
                      Remove
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="col-lg-4">
          <div className="card shadow-sm">
            <div className="card-body">
              <h4 className="fw-bold">Order Summary</h4>

              <hr />

              <div className="d-flex justify-content-between">
                <span>Items</span>
                <span>{cartItems.length}</span>
              </div>

              <div className="d-flex justify-content-between mt-3">
                <span>Total</span>

                <strong>₹{total.toFixed(2)}</strong>
              </div>

              <Link to="/checkout" className="btn btn-primary w-100 mt-4">
                Proceed to Checkout
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CartPage;
