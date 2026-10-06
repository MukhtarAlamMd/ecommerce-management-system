import { useState } from "react";

function MessageClassifier() {
  const [message, setMessage] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const classifyMessage = async () => {
    if (!message.trim()) {
      setError("Please enter a customer message.");
      return;
    }

    setLoading(true);
    setError("");
    setResult(null);

    try {
      const response = await fetch(
        "http://localhost:8089/api/messages/classify",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            message: message,
          }),
        },
      );

      if (!response.ok) {
        throw new Error("Failed to classify message.");
      }

      const data = await response.json();

      setResult(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: "700px", margin: "40px auto" }}>
      <h2>AI Customer Message Classifier</h2>

      <textarea
        rows="6"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        placeholder="Enter customer message..."
        style={{
          width: "100%",
          padding: "12px",
          marginTop: "15px",
          fontSize: "16px",
        }}
      />

      <button
        onClick={classifyMessage}
        disabled={loading}
        style={{
          marginTop: "15px",
          padding: "10px 20px",
          cursor: loading ? "not-allowed" : "pointer",
        }}
      >
        {loading ? "Classifying..." : "Classify Message"}
      </button>

      {error && <p style={{ color: "red", marginTop: "15px" }}>{error}</p>}

      {result && (
        <div
          style={{
            marginTop: "25px",
            padding: "20px",
            border: "1px solid #ddd",
            borderRadius: "8px",
          }}
        >
          <h3>Classification Result</h3>

          <p>
            <strong>Category:</strong> {result.category}
          </p>

          <p>
            <strong>Priority:</strong> {result.priority}
          </p>

          <p>
            <strong>Confidence:</strong> {(result.confidence * 100).toFixed(0)}%
          </p>
        </div>
      )}
    </div>
  );
}

export default MessageClassifier;
