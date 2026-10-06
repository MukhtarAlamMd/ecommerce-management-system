import { useState } from "react";
import aiService from "../services/aiService";
import "./AISupport.css";

const exampleMessages = [
  "Payment was deducted but my order is still pending",
  "I received the wrong product",
  "My order has not been delivered yet",
];

const AISupport = () => {
  const [message, setMessage] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleClassify = async () => {
    if (!message.trim()) {
      setError("Please describe your issue before analyzing.");
      setResult(null);
      return;
    }

    try {
      setLoading(true);
      setError("");
      setResult(null);

      const data = await aiService.classifyMessage(message);

      setResult(data);
    } catch (err) {
      console.error("AI classification error:", err);
      setError(err.message || "Unable to analyze your message.");
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setMessage("");
    setResult(null);
    setError("");
  };

  const useExample = (example) => {
    setMessage(example);
    setResult(null);
    setError("");
  };

  const confidence =
    result?.confidence !== undefined ? Math.round(result.confidence * 100) : 0;

  const getPriorityClass = (priority) => {
    if (!priority) return "";

    switch (priority.toUpperCase()) {
      case "HIGH":
        return "priority-high";

      case "MEDIUM":
        return "priority-medium";

      case "LOW":
        return "priority-low";

      default:
        return "priority-default";
    }
  };

  const getCategoryLabel = (category) => {
    if (!category) return "Unknown Issue";

    return category
      .replaceAll("_", " ")
      .toLowerCase()
      .replace(/\b\w/g, (letter) => letter.toUpperCase());
  };

  return (
    <div className="ai-support-page">
      {/* Header */}
      <div className="ai-page-header">
        <div className="ai-title-section">
          <div className="ai-icon">🤖</div>

          <div>
            <h1>AI Customer Support</h1>

            <p>
              Let AI understand customer issues and identify their category and
              priority.
            </p>
          </div>
        </div>

        <div className="ai-status">
          <span className="status-dot"></span>
          AI Service Online
        </div>
      </div>

      {/* Main Content */}
      <div className="ai-support-grid">
        {/* LEFT - Message */}
        <div className="ai-card message-card">
          <div className="card-header">
            <div>
              <h2>Describe the Issue</h2>

              <p>Enter the customer's message below.</p>
            </div>

            <span className="step-number">01</span>
          </div>

          <div className="message-input-wrapper">
            <textarea
              id="customer-message"
              value={message}
              onChange={(e) => {
                setMessage(e.target.value);
                setError("");
              }}
              placeholder="Example: Payment was deducted but my order is still pending..."
              rows={8}
              maxLength={1000}
              disabled={loading}
            />

            <div className="character-count">{message.length}/1000</div>
          </div>

          {/* Examples */}
          <div className="examples-section">
            <span className="examples-title">Try an example</span>

            <div className="example-buttons">
              {exampleMessages.map((example, index) => (
                <button
                  key={index}
                  type="button"
                  className="example-button"
                  onClick={() => useExample(example)}
                  disabled={loading}
                >
                  {example}
                </button>
              ))}
            </div>
          </div>

          {/* Error */}
          {error && (
            <div className="ai-error">
              <span className="error-icon">!</span>

              <div>
                <strong>Unable to analyze</strong>
                <p>{error}</p>
              </div>
            </div>
          )}

          {/* Actions */}
          <div className="ai-actions">
            <button
              type="button"
              className="clear-button"
              onClick={handleClear}
              disabled={loading || !message}
            >
              Clear
            </button>

            <button
              type="button"
              className="analyze-button"
              onClick={handleClassify}
              disabled={loading || !message.trim()}
            >
              {loading ? (
                <>
                  <span className="spinner"></span>
                  Analyzing...
                </>
              ) : (
                <>✨ Analyze with AI</>
              )}
            </button>
          </div>
        </div>

        {/* RIGHT - Result */}
        <div className="ai-card result-card">
          <div className="card-header">
            <div>
              <h2>AI Analysis</h2>

              <p>Your AI-powered assessment appears here.</p>
            </div>

            <span className="step-number">02</span>
          </div>

          {!result && !loading && (
            <div className="empty-result">
              <div className="empty-ai-icon">🤖</div>

              <h3>Ready to analyze</h3>

              <p>
                Enter a customer message and click
                <strong> Analyze with AI </strong>
                to see the result.
              </p>
            </div>
          )}

          {loading && (
            <div className="analyzing-state">
              <div className="large-spinner"></div>

              <h3>AI is analyzing...</h3>

              <p>
                Understanding the customer's issue and determining its priority.
              </p>
            </div>
          )}

          {result && !loading && (
            <div className="result-content">
              {/* Category */}
              <div className="result-box">
                <div className="result-icon category-icon">🏷️</div>

                <div className="result-info">
                  <span>Issue Category</span>

                  <strong>{getCategoryLabel(result.category)}</strong>
                </div>
              </div>

              {/* Priority */}
              <div className="result-box">
                <div className="result-icon priority-icon">⚡</div>

                <div className="result-info">
                  <span>Priority</span>

                  <strong className={getPriorityClass(result.priority)}>
                    {result.priority || "N/A"}
                  </strong>
                </div>
              </div>

              {/* Confidence */}
              <div className="confidence-box">
                <div className="confidence-header">
                  <span>AI Confidence</span>

                  <strong>{confidence}%</strong>
                </div>

                <div className="confidence-bar">
                  <div
                    className="confidence-fill"
                    style={{
                      width: `${confidence}%`,
                    }}
                  ></div>
                </div>

                <p>The AI is {confidence}% confident in this classification.</p>
              </div>

              {/* Summary */}
              <div className="ai-summary">
                <div className="summary-icon">💡</div>

                <div>
                  <strong>AI Recommendation</strong>

                  <p>
                    This message has been classified as a{" "}
                    <strong>{getCategoryLabel(result.category)}</strong> with{" "}
                    <strong>{result.priority || "unknown"}</strong> priority.
                  </p>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Footer information */}
      <div className="ai-footer">
        <span>🔒 Secure AI Analysis</span>

        <span>•</span>

        <span>⚡ Fast Classification</span>

        <span>•</span>

        <span>🎯 AI Confidence Scoring</span>
      </div>
    </div>
  );
};

export default AISupport;
