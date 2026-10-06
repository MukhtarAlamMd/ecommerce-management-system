// =========================================================
// GET CART
// =========================================================

const getCart = () => {
  const cart = localStorage.getItem("cart");

  return cart ? JSON.parse(cart) : [];
};

// =========================================================
// SAVE CART
// =========================================================

const saveCart = (cart) => {
  localStorage.setItem("cart", JSON.stringify(cart));
};

// =========================================================
// ADD TO CART
// =========================================================

const addToCart = (product) => {
  const cart = getCart();

  const existingProduct = cart.find((item) => item.id === product.id);

  if (existingProduct) {
    existingProduct.quantity += 1;
  } else {
    cart.push({
      ...product,
      quantity: 1,
    });
  }

  saveCart(cart);

  return cart;
};

// =========================================================
// REMOVE FROM CART
// =========================================================

const removeFromCart = (productId) => {
  const cart = getCart();

  const updatedCart = cart.filter((item) => item.id !== productId);

  saveCart(updatedCart);

  return updatedCart;
};

// =========================================================
// UPDATE QUANTITY
// =========================================================

const updateQuantity = (productId, quantity) => {
  if (quantity <= 0) {
    return removeFromCart(productId);
  }

  const cart = getCart();

  const updatedCart = cart.map((item) =>
    item.id === productId ? { ...item, quantity } : item,
  );

  saveCart(updatedCart);

  return updatedCart;
};

// =========================================================
// CLEAR CART
// =========================================================

const clearCart = () => {
  localStorage.removeItem("cart");
};

// =========================================================
// EXPORT
// =========================================================

export default {
  getCart,
  saveCart,
  addToCart,
  removeFromCart,
  updateQuantity,
  clearCart,
};
