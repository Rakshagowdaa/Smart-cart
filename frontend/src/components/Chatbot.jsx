import React, { useState } from 'react';

function Chatbot() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { sender: 'bot', text: 'Hi there! I am the Smart Cart AI assistant. How can I help you today?' }
  ]);
  const [input, setInput] = useState('');

  const handleSend = () => {
    if (!input.trim()) return;
    const userMsg = { sender: 'user', text: input };
    setMessages((prev) => [...prev, userMsg]);
    
    // Simulate AI response
    setTimeout(() => {
      let botMsg = { sender: 'bot', text: "I'm sorry, I'm a demo bot and can only answer basic FAQs." };
      const lowerInput = input.toLowerCase();
      
      if (lowerInput.includes('order')) {
        botMsg.text = "You can track your orders in the 'Orders' tab from your profile.";
      } else if (lowerInput.includes('password')) {
        botMsg.text = "If you forgot your password, you can reset it using the 'Forgot Password' link on the Login page.";
      } else if (lowerInput.includes('payment') || lowerInput.includes('pay')) {
        botMsg.text = "We support Razorpay, which accepts Credit Cards, UPI, and Netbanking.";
      } else if (lowerInput.includes('hi') || lowerInput.includes('hello')) {
        botMsg.text = "Hello! Looking for any specific product today?";
      }

      setMessages((prev) => [...prev, botMsg]);
    }, 1000);

    setInput('');
  };

  return (
    <div style={styles.chatbotContainer}>
      {!isOpen && (
        <button style={styles.bubble} onClick={() => setIsOpen(true)}>
          💬
        </button>
      )}

      {isOpen && (
        <div style={styles.chatWindow}>
          <div style={styles.header}>
            <span style={{ fontWeight: 'bold', color: 'white' }}>Smart Cart AI</span>
            <button style={styles.closeBtn} onClick={() => setIsOpen(false)}>✖</button>
          </div>
          
          <div style={styles.messagesContainer}>
            {messages.map((msg, index) => (
              <div
                key={index}
                style={{
                  ...styles.message,
                  alignSelf: msg.sender === 'user' ? 'flex-end' : 'flex-start',
                  backgroundColor: msg.sender === 'user' ? '#0d6efd' : '#f1f1f1',
                  color: msg.sender === 'user' ? 'white' : 'black'
                }}
              >
                {msg.text}
              </div>
            ))}
          </div>

          <div style={styles.inputArea}>
            <input
              type="text"
              style={styles.input}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSend()}
              placeholder="Ask a question..."
            />
            <button style={styles.sendBtn} onClick={handleSend}>Send</button>
          </div>
        </div>
      )}
    </div>
  );
}

const styles = {
  chatbotContainer: {
    position: 'fixed',
    bottom: '20px',
    right: '20px',
    zIndex: 9999,
  },
  bubble: {
    width: '60px',
    height: '60px',
    borderRadius: '50%',
    backgroundColor: '#0d6efd',
    color: 'white',
    fontSize: '24px',
    border: 'none',
    boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  },
  chatWindow: {
    width: '320px',
    height: '400px',
    backgroundColor: 'white',
    borderRadius: '10px',
    boxShadow: '0 5px 15px rgba(0,0,0,0.2)',
    display: 'flex',
    flexDirection: 'column',
    overflow: 'hidden'
  },
  header: {
    backgroundColor: '#0d6efd',
    padding: '12px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  },
  closeBtn: {
    background: 'none',
    border: 'none',
    color: 'white',
    cursor: 'pointer',
    fontWeight: 'bold'
  },
  messagesContainer: {
    flex: 1,
    padding: '10px',
    overflowY: 'auto',
    display: 'flex',
    flexDirection: 'column',
    gap: '8px'
  },
  message: {
    padding: '8px 12px',
    borderRadius: '15px',
    maxWidth: '80%',
    fontSize: '14px',
    wordWrap: 'break-word'
  },
  inputArea: {
    display: 'flex',
    padding: '10px',
    borderTop: '1px solid #ddd'
  },
  input: {
    flex: 1,
    padding: '8px',
    border: '1px solid #ccc',
    borderRadius: '5px',
    marginRight: '8px',
    outline: 'none'
  },
  sendBtn: {
    padding: '8px 12px',
    backgroundColor: '#0d6efd',
    color: 'white',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer'
  }
};

export default Chatbot;
