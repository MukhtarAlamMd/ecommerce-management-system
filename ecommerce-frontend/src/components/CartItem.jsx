import { useCart } from "../context/CartContext";

const CartItem = ({ item }) => {
  const { increaseQuantity, decreaseQuantity, removeFromCart } = useCart();

  const itemTotal = Number(item.price) * item.quantity;

  return (
    <div className="card mb-3 shadow-sm">
      <div className="card-body">
        <div className="row align-items-center">
          {/* PRODUCT */}
          <div className="col-md-4">
            <h5 className="fw-bold">{item.name}</h5>

            <p className="text-muted mb-1">{item.categoryName || "Product"}</p>

            <p className="mb-0">₹{Number(item.price).toFixed(2)}</p>
          </div>

          {/* QUANTITY */}
          <div className="col-md-3">
            <label className="form-label fw-bold">Quantity</label>

            <div className="input-group">
              <button
                className="btn btn-outline-secondary"
                onClick={() => decreaseQuantity(item.id)}
              >
                −
              </button>

              <span className="input-group-text">{item.quantity}</span>

              <button
                className="btn btn-outline-secondary"
                disabled={item.quantity >= item.stockQuantity}
                onClick={() => increaseQuantity(item.id)}
              >
                +
              </button>
            </div>

            <small className="text-muted">Stock: {item.stockQuantity}</small>
          </div>

          {/* TOTAL */}
          <div className="col-md-3">
            <strong>₹{itemTotal.toFixed(2)}</strong>
          </div>

          {/* REMOVE */}
          <div className="col-md-2">
            <button
              className="btn btn-outline-danger btn-sm"
              onClick={() => removeFromCart(item.id)}
            >
              Remove
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CartItem;
