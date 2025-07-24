import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;


class Stock {
    String symbol, name;
    double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }
}

class User {
    double balance;
    HashMap<String, Integer> portfolio = new HashMap<>();
    ArrayList<String> transactionHistory = new ArrayList<>();

    public User(double balance) {
        this.balance = balance;
    }

    public void buyStock(String symbol, int qty, double price) {
        double cost = qty * price;
        if (balance >= cost) {
            balance -= cost;
            portfolio.put(symbol, portfolio.getOrDefault(symbol, 0) + qty);
            logTransaction("Bought", symbol, qty, price);
        }
    }

    public void sellStock(String symbol, int qty, double price) {
        if (portfolio.getOrDefault(symbol, 0) >= qty) {
            balance += qty * price;
            portfolio.put(symbol, portfolio.get(symbol) - qty);
            if (portfolio.get(symbol) == 0) portfolio.remove(symbol);
            logTransaction("Sold", symbol, qty, price);
        }
    }

    public void logTransaction(String type, String symbol, int qty, double price) {
        transactionHistory.add(type + " " + qty + " of " + symbol + " @ INR " + price);
    }

    public String viewPortfolio() {
        StringBuilder sb = new StringBuilder("--- Portfolio ---\n");
        for (String key : portfolio.keySet()) {
            sb.append(key).append(" -> ").append(portfolio.get(key)).append("\n");
        }
        sb.append("Balance: INR ").append(balance);
        return sb.toString();
    }

    public String viewTransactionHistory() {
        StringBuilder sb = new StringBuilder("--- Transaction History ---\n");
        for (String record : transactionHistory) {
            sb.append(record).append("\n");
        }
        return sb.toString();
    }

    public double getPortfolioValue(HashMap<String, Stock> market) {
        double total = 0;
        for (String symbol : portfolio.keySet()) {
            if (market.containsKey(symbol)) {
                total += market.get(symbol).price * portfolio.get(symbol);
            }
        }
        return total;
    }
}

public class GUIStockTrading extends JFrame {
    JTextField symbolField, qtyField;
    JTextArea output;
    JLabel balanceLabel;
    User user = new User(100000);
    HashMap<String, Stock> market = new HashMap<>();

    public GUIStockTrading() {
        market.put("TCS", new Stock("TCS", "Tata Consultancy", 3500));
        market.put("INFY", new Stock("INFY", "Infosys", 1450));
        market.put("WIPRO", new Stock("WIPRO", "Wipro Ltd.", 600));
        market.put("RELIANCE", new Stock("RELIANCE", "Reliance Industries", 2800));

        setTitle("Stock Trading GUI");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        symbolField = new JTextField(10);
        qtyField = new JTextField(5);
        JButton view = new JButton("View Market");
        JButton buy = new JButton("Buy");
        JButton sell = new JButton("Sell");
        JButton port = new JButton("Portfolio");
        JButton balanceBtn = new JButton("Show Balance");
        JButton historyBtn = new JButton("View History");
        JButton valueBtn = new JButton("Portfolio Value");

        output = new JTextArea();
        output.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(output);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Symbol:"));
        panel.add(symbolField);
        panel.add(new JLabel("Qty:"));
        panel.add(qtyField);
        panel.add(view);
        panel.add(buy);
        panel.add(sell);
        panel.add(port);
        panel.add(balanceBtn);
        panel.add(historyBtn);
        panel.add(valueBtn);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        view.addActionListener(e -> {
            output.setText("--- Market Stocks ---\n");
            for (Stock s : market.values()) {
                output.append(s.symbol + " - " + s.name + " : INR " + s.price + "\n");
            }
        });

        buy.addActionListener(e -> {
            try {
                String sym = symbolField.getText().toUpperCase();
                int qty = Integer.parseInt(qtyField.getText());
                if (market.containsKey(sym)) {
                    user.buyStock(sym, qty, market.get(sym).price);
                    output.setText("Bought " + qty + " of " + sym);
                } else {
                    output.setText("Stock not found");
                }
            } catch (Exception ex) {
                output.setText("Invalid input");
            }
        });

        sell.addActionListener(e -> {
            try {
                String sym = symbolField.getText().toUpperCase();
                int qty = Integer.parseInt(qtyField.getText());
                if (market.containsKey(sym)) {
                    user.sellStock(sym, qty, market.get(sym).price);
                    output.setText("Sold " + qty + " of " + sym);
                } else {
                    output.setText("Stock not found");
                }
            } catch (Exception ex) {
                output.setText("Invalid input");
            }
        });

        port.addActionListener(e -> output.setText(user.viewPortfolio()));
        balanceBtn.addActionListener(e -> output.setText("Balance: INR " + user.balance));
        historyBtn.addActionListener(e -> output.setText(user.viewTransactionHistory()));
        valueBtn.addActionListener(e -> output.setText("Portfolio Value: INR " + user.getPortfolioValue(market)));

        // Auto-update stock prices every 10 seconds
        Timer timer = new Timer(10000, e -> updateStockPrices());
        timer.start();

        setVisible(true);
    }

    public void updateStockPrices() {
        Random rand = new Random();
        for (Stock s : market.values()) {
            double change = (rand.nextDouble() * 2 - 1) * 0.05; // ±5%
            s.price += s.price * change;
            s.price = Math.round(s.price * 100.0) / 100.0;
        }
        output.append("\n[Market updated with new stock prices]\n");
    }

    public static void main(String[] args) {
        new GUIStockTrading();
    }
}
