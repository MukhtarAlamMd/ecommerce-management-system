import { createContext, useContext, useEffect, useState } from "react";

import inventoryService from "../services/inventoryService";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const [cartItems, setCartItems] = useState(() => {
    const savedCart = localStorage.getItem("cart");

    try {
      return savedCart ? JSON.parse(savedCart) : [];
    } catch (error) {
      console.error("Invalid cart data:", error);
      return [];
    }
  });

  const [cartLoading, setCartLoading] = useState(false);

  // =====================================================
  // SAVE CART
  // =====================================================

  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cartItems));
  }, [cartItems]);

  // =====================================================
  // ADD TO CART
  // =====================================================

  const addToCart = async (product, requestedQuantity = 1) => {
    if (!product?.id) {
      throw new Error("Invalid product");
    }

    try {
      setCartLoading(true);

      // -------------------------------------------------
      // GET REAL STOCK FROM INVENTORY SERVICE
      // -------------------------------------------------

      const inventory = await inventoryService.getInventoryByProductId(
        product.id,
      );

      const availableQuantity = Number(inventory?.availableQuantity ?? 0);

      // -------------------------------------------------
      // CHECK INVENTORY STATUS
      // -------------------------------------------------

      if (
        inventory?.enabled !== true ||
        product.enabled !== true ||
        availableQuantity <= 0
      ) {
        throw new Error("Product is currently out of stock");
      }

      const quantity = Number(requestedQuantity);

      if (!Number.isInteger(quantity) || quantity <= 0) {
        throw new Error("Invalid quantity");
      }

      if (quantity > availableQuantity) {
        throw new Error(`Only ${availableQuantity} item(s) are available`);
      }

      // -------------------------------------------------
      // ADD / UPDATE CART
      // -------------------------------------------------

      setCartItems((currentItems) => {
        const existingItem = currentItems.find(
          (item) => item.id === product.id,
        );

        if (existingItem) {
          const newQuantity = Math.min(
            existingItem.quantity + quantity,
            availableQuantity,
          );

          return currentItems.map((item) =>
            item.id === product.id
              ? {
                  ...item,

                  // Update with REAL inventory stock
                  stockQuantity: availableQuantity,

                  quantity: newQuantity,
                }
              : item,
          );
        }

        return [
          ...currentItems,

          {
            id: product.id,
            name: product.name,
            description: product.description,
            categoryId: product.categoryId,
            categoryName: product.categoryName,
            price: product.price,
            enabled: product.enabled,

            // IMPORTANT:
            // Stock comes from Inventory Service
            stockQuantity: availableQuantity,

            quantity,
          },
        ];
      });
    } finally {
      setCartLoading(false);
    }
  };

  // =====================================================
  // REMOVE FROM CART
  // =====================================================

  const removeFromCart = (productId) => {
    setCartItems((currentItems) =>
      currentItems.filter((item) => item.id !== productId),
    );
  };

  // =====================================================
  // INCREASE QUANTITY
  // =====================================================

  const increaseQuantity = async (productId) => {
    try {
      // -------------------------------------------------
      // GET CURRENT INVENTORY
      // -------------------------------------------------

      const inventory =
        await inventoryService.getInventoryByProductId(productId);

      const availableQuantity = Number(inventory?.availableQuantity ?? 0);

      if (inventory?.enabled !== true || availableQuantity <= 0) {
        throw new Error("Product is out of stock");
      }

      setCartItems((currentItems) =>
        currentItems.map((item) => {
          if (item.id !== productId) {
            return item;
          }

          if (item.quantity >= availableQuantity) {
            return {
              ...item,
              stockQuantity: availableQuantity,
            };
          }

          return {
            ...item,

            stockQuantity: availableQuantity,

            quantity: item.quantity + 1,
          };
        }),
      );
    } catch (error) {
      console.error("Failed to increase quantity:", error);

      throw error;
    }
  };

  // =====================================================
  // DECREASE QUANTITY
  // =====================================================

  const decreaseQuantity = (productId) => {
    setCartItems((currentItems) =>
      currentItems
        .map((item) => {
          if (item.id !== productId) {
            return item;
          }

          return {
            ...item,
            quantity: item.quantity - 1,
          };
        })
        .filter((item) => item.quantity > 0),
    );
  };

  // =====================================================
  // UPDATE QUANTITY
  // =====================================================

  const updateQuantity = async (productId, requestedQuantity) => {
    const quantity = Number(requestedQuantity);

    if (!Number.isInteger(quantity) || quantity <= 0) {
      removeFromCart(productId);
      return;
    }

    try {
      // -------------------------------------------------
      // GET REAL INVENTORY
      // -------------------------------------------------

      const inventory =
        await inventoryService.getInventoryByProductId(productId);

      const availableQuantity = Number(inventory?.availableQuantity ?? 0);

      if (inventory?.enabled !== true || availableQuantity <= 0) {
        throw new Error("Product is out of stock");
      }

      // -------------------------------------------------
      // LIMIT TO REAL STOCK
      // -------------------------------------------------

      const finalQuantity = Math.min(quantity, availableQuantity);

      setCartItems((currentItems) =>
        currentItems.map((item) =>
          item.id === productId
            ? {
                ...item,

                stockQuantity: availableQuantity,

                quantity: finalQuantity,
              }
            : item,
        ),
      );
    } catch (error) {
      console.error("Failed to update quantity:", error);

      throw error;
    }
  };

  // =====================================================
  // REFRESH CART STOCK
  // =====================================================

  const refreshCartStock = async () => {
    if (cartItems.length === 0) {
      return;
    }

    try {
      const updatedItems = await Promise.all(
        cartItems.map(async (item) => {
          try {
            const inventory = await inventoryService.getInventoryByProductId(
              item.id,
            );

            const availableQuantity = Number(inventory?.availableQuantity ?? 0);

            return {
              ...item,
              stockQuantity: availableQuantity,
              inventoryEnabled: inventory?.enabled === true,
            };
          } catch (error) {
            console.error(
              `Failed to load inventory for product ${item.id}:`,
              error,
            );

            return {
              ...item,
              stockQuantity: 0,
              inventoryEnabled: false,
            };
          }
        }),
      );

      setCartItems(updatedItems);
    } catch (error) {
      console.error("Failed to refresh cart stock:", error);
    }
  };

  // =====================================================
  // CLEAR CART
  // =====================================================

  const clearCart = () => {
    setCartItems([]);
  };

  // =====================================================
  // TOTAL ITEMS
  // =====================================================

  const getCartItemCount = () => {
    return cartItems.reduce(
      (total, item) => total + Number(item.quantity || 0),
      0,
    );
  };

  // =====================================================
  // TOTAL PRICE
  // =====================================================

  const getCartTotal = () => {
    return cartItems.reduce(
      (total, item) =>
        total + Number(item.price || 0) * Number(item.quantity || 0),
      0,
    );
  };

  // =====================================================
  // CONTEXT VALUE
  // =====================================================

  const value = {
    cartItems,

    cartLoading,

    addToCart,

    removeFromCart,

    increaseQuantity,

    decreaseQuantity,

    updateQuantity,

    refreshCartStock,

    clearCart,

    getCartItemCount,

    getCartTotal,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};

// =====================================================
// USE CART
// =====================================================

export const useCart = () => {
  return useContext(CartContext);
};
