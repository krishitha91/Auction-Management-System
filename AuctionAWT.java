import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ---------------------------------------------------------
// BidRecord class
// ---------------------------------------------------------
class BidRecord {
    String bidder;
    double amount;CLEAR
    String date;
    String day;
    String time;

    BidRecord(String bidder, double amount) {
        this.bidder = bidder;
        this.amount = amount;

        LocalDateTime now = LocalDateTime.now();
        date = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        day = now.format(DateTimeFormatter.ofPattern("EEEE"));
        time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    BidRecord(String bidder, double amount, String date,
            String day, String time) {
        this.bidder = bidder;
        this.amount = amount;
        this.date = date;
        this.day = day;
        this.time = time;
    }

    String toCSV(int itemId) {
        return itemId + "," + clean(bidder) + "," + amount + ","
                + date + "," + day + "," + time;
    }

    private String clean(String value) {
        return value.replace(",", " ");
    }
}

// ---------------------------------------------------------
// Item class
// ---------------------------------------------------------
class Item {
    int id;
    String title;
    String category;
    String seller;
    double startPrice;
    double currentBid;
    String highestBidder;
    String status;

    ArrayList<BidRecord> bidHistory = 
    Item(int id, String title, String category, String seller,
            double startPrice) {
            his.id = id;
        this.title = title;
        this.category = category;
        this.seller = seller;
        this.startPrice = startPrice;
        this.currentBid = 0;
        this.highestBidder = "-";
        this.status = "Open";
    }

    int getBidderCount() {
        Set<String> names = new HashSet<String>();

        for (BidRecord bid : bidHistory) {
            names.add(bid.bidder.toLowerCase());
        }

        return names.size();
    }

    double getTotalBidAmount() {
        double total = 0;

        for (BidRecord bid : bidHistory) {
            total += bid.amount;
        }

        return total;
    }

    public String toString() {
        return id + " | " + title + " | " + category
                + " | Seller: " + seller
                + " | Start: " + startPrice
                + " | Highest Bid: " + currentBid
                + " | By: " + highestBidder
                + " | " + status
                + " | Bidders: " + getBidderCount();
    }

    String toCSV() {
        return id + "," + clean(title) + "," + clean(category) + ","
                + clean(seller) + "," + startPrice + "," + currentBid
                + "," + clean(highestBidder) + "," + clean(status);
    }

    private String clean(String value) {
        return value.replace(",", " ");
    }

    static Item fromCSV(String line) {
        String[] p = line.split(",", -1);

        if (p.length < 8)
            return null;
            

        try {
            Item it = new Item(
                    Integer.parseInt(p[0]),
                    p[1],
                    p[2],
                    p[3],
                    Double.parseDouble(p[4])
            it.currentBid = Double.parseDouble(p[5]);
            it.highestBidder = p[6];
            it.status = p[7];

            return it;

        } catch (NumberFormatException e) {
            return null;
        }
    }
}

// ---------------------------------------------------------
// Main Auction Management System using AWT
// ---------------------------------------------------------
public class AuctionAWT extends Frame
        implements ActionListener {

    // Data and CSV file names
    private ArrayList<Item> items = new ArrayList<Item>();
 int nextId = 1001;

    // Stores seller name after first entry
    private String savedSellerName = "";

    private final String FILE_NAME = "auction_awt.csv";
    private final String BID_FILE = "auction_bids_awt.csv";

    // Input components
    private TextField titleField, sellerField, priceField;
    private TextField bidderField, bidField, searchField;

    private Choice categoryChoice;

    // Display components
    private List itemList;
    private Label statusLabel;

    // Buttons
    private Button addBtn, updateBtn, deleteBtn;
    private Button bidBtn, closeBtn;
    private Button searchBtn, saveBtn, loadBtn;
    private Button clearBtn, summaryBtn;

    public AuctionAWT() {

        setTitle("Auction Management System (AWT)");
        setSize(1000, 620);
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(220, 230, 245));

        buildTopPanel();
        buildCenterPanel();
        buildBottomPanel();

        itemList.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                fillFormFromSelection();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveData();
                dispose();
                System.exit(0);
            }
        });

        loadData();
        loadBidData();
        refreshList();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // -----------------------------------------------------
    // Layout building methods
    // -----------------------------------------------------
    private void buildTopPanel() {

        Panel form = new Panel(
                new GridLayout(6, 2, 10, 8));

        form.add(new Label("Item Title:"));titleField = new TextField();
        form.add(titleField);

        form.add(new Label("Category:"));
        categoryChoice = new Choice();

        categoryChoice.add("Collectibles");
        categoryChoice.add("Antiques");
        categoryChoice.add("Real Estate");
        categoryChoice.add("Art");
        categoryChoice.add("Electronics");
        categoryChoice.add("Others");

        form.add(categoryChoice);

        form.add(new Label("Seller Name:"));
        sellerField = new TextField();
        form.add(sellerField);

        form.add(new Label("Starting Price:"));
        priceField = new TextField();
        form.add(priceField);

        form.add(new Label("Bidder Name:"));
        bidderField = new TextField();
        form.add(bidderField);

        form.add(new Label("Bid Amount:"));
        bidField = new TextField();
        form.add(bidField);

        // Search area
        Panel search = new Panel(
                new FlowLayout(FlowLayout.LEFT));

        search.add(new Label("Search Title:"));
searchField = new TextField(15);
        search.add(searchField);

        searchBtn = new Button("Search");
        searchBtn.addActionListener(this);
        search.add(searchBtn);

        Panel top = new Panel(
                new BorderLayout(10, 10));

        top.add(form, BorderLayout.CENTER);
        top.add(search, BorderLayout.EAST);
add(top, BorderLayout.NORTH);
    }

    private void buildCenterPanel() {

        itemList = new List(14, false);

        itemList.setBackground(Color.WHITE);

        itemList.setFont(
                new Font("Monospaced", Font.PLAIN, 12));

        add(itemList, BorderLayout.CENTER);
    }

    private void buildBottomPanel() {
        Panel buttons = new Panel(
                new FlowLayout());

        addBtn = makeButton("List Item", buttons);
        updateBtn = makeButton("Update", buttons);
        deleteBtn = makeButton("Delete", buttons);
        bidBtn = makeButton("Place Bid", buttons);
        closeBtn = makeButton("C
        summaryBtn = makeButton("Bid Summary", buttons);
        saveBtn = makeButton("Save CSV", buttons);
        loadBtn = makeButton("Load CSV", buttons);
        clearBtn = makeButton("Clear Form", buttons);

        statusLabel = new Label(
                "Welcome to the Auction Management System.");

        statusLabel.setForeground(
                new Color(0, 70, 0));

        Panel bottom = new Panel(
                new GridLayout(2, 1));
bottom.add(buttons);
        bottom.add(statusLabel);

        add(bottom, BorderLayout.SO

    private Button makeButton(String text, Panel panel) {
Button b = new Button(text);

        b.addActionListener(this);

        panel.add(b);

        return b;
    }

    // -----------------------------------------------------
    // Event handling
    // -----------------------------------------------------
    public void actionPerformed(ActionEvent e) {

        Object src = e.getSource();

        if (src == addBtn)
            addItem();

        else if (src == updateBtn)
            updateItem();

        else if (src == deleteBtn)
            deleteItem();

        else if (src == bidBtn)
            placeBid();

        else if (src == closeBtn)
            closeAuction();

        else if (src == searchBtn)
            searchItem();

        else if (src == summaryBtn)
            showBidSummary();

        else if (src == saveBtn) {
            saveData();
            showStatus("Auction and bid records saved.", false);
        }

        else if (src == loadBtn) {
            loadData();
            loadBidData();
            refreshList();

            showStatus(
                    "Auction and bid records loaded.", false);
        }

        else if (src == clearBtn) {
            clearForm();
            showStatus("Form cleared.", false);
        }
    }

    // -----------------------------------------------------
    // Auction operations------------------------------------------------

    // Seller name is requested only for the first item
    private void addItem() {

        String title = titleField.getText().trim();

        String seller;

        if (savedSellerName.isEmpty()) {
            seller = sellerField.getText().trim();
        } else {
            seller = savedSellerName;
        }

        if (title.isEmpty() || seller.isEmpty()) {
            showStatus(
                    "Item title and seller name are required.",
                    true);
            return;
        }

        double price;

        try {
            price = Double.parseDouble(
                    priceField.getText().trim());

            if (price <= 0)
                throw ne
        } catch (NumberFormatException ex) {

            showStatus(
                    "Enter a valid starting price.", true);
            return;
        }

        // Save seller name after the first sucsavedSellerName.isEmpty()) {

            savedSellerName = seller;

            sellerField.setText(savedSellerName);

            sellerField.setEditable(false);
        }
s.add(new Item(
                nextId++,
                title,
                categoryChoice.getSelectedItem(),
                seller,
                price));

        refreshList();
        clearForm();

        showStatus("Item listed successfully.", false);
    }

    private void updateItem() {

        Item it = getSelectedItem();

        if (it == null) {
            showStatus("Select an item to update.", true);
            return;}

        if (!it.status.equals("Open") || it.currentBid > 0) {
            showStatus(
                    "Only open items without bids can be updated.",
                    true);
            return;
        }

        try {
            double price = Double.parseDouble(
                    priceField.getText().trim());

            if (price <= 0)
                throw new NumberFormatException();

            it.title = titleField.getText().trim();
            it.category = categoryChoice.getSelectedItem();

            // Keep the original saved seller name
            it.seller = 
            it.startPrice = price;

            int index = itemList.getSelectedIndex();

            refreshList();
            itemList.select(index);
            showStatus("Item updated.", false);

        } catch (NumberFormatException ex) {

            showStatus(
                    "Enter a valid starting price.", true);
        }
    }

    private void deleteItem() {

        int index = itemList.getSelectedIndex();

        if (index < 0) {
            showStatus("Select an item to delete.", true);
            return;
        }

        items.remove(index);

        refreshList();
        clearForm();
Data();

        showStatus(
                "Item and its bid history deleted.", false);
    }

    // -----------------------------------------------------
    // Place Bid
    // -----------------------------------------------------
    private void placeBid() {

        Item it = getSelectedItem();

        if (it == null) {
            showStatus("Select an item to bid on.", true);
            return;
        }

        if (!it.status.equals("Open")) {
            showStatus("This auction is closed.", true);
            return;
        }
        // Bidder name is entered for every bid
        String bidder = bidderField.getText().trim();

        if (bidder.isEmpty()) {
            showStatus("Bidder name is required.", true);
            return;
        }

        if (bidder.equalsIgnoreCase(it.seller)) {
            showStatus(
                    "Sellers cannot bid on their own items.",
                    true);
            return;
        }

        try {

            double bid = Double.parseDouble(
                    bidField.getText().trim());

            boolean firstBid = (it.currentBid == 0);

            double minimum = Math.max(
                    it.startPrice, it.currentBid);

            if (bid <= 0 || bid < minimum
                    || (!firstBid && bid <= minimum)) {

                showStatus(
                        "Bid must be higher than " + minimum,
                            return;
            }

            // Save bidder, amount, date, day and time
            it.currentBid = bid;
            it.highestBidder = bidder;

            it.bidHistory.add(        new BidRecord(bidder, bid));

            int index = itemList.getSelectedIndex();

            refreshList();
            itemList.select(index);
            // Clear bidder and bid amount after each bid
            bidderField.setText("");
            bidField.setText("");

            saveData();

            showStatus(    "Bid accepted. " + bidder
                            + " is the highest bidder.",
                    false);

        } catch (NumberFormatException ex) {

            showStatus(
                    "Enter a valid bid amount.", true);
        }
    }
    // -----------------------------------------------------
    // Close Auction
    // -----------------------------------------------------
    private void closeAuction() {

        Item it = getSelectedItem();

        if (it == null) {
            showStatus("Select an item to close.", true);
            return;
        }

        if (!it.status.equals("Open")) {
            showStatus(
                    "This auction is already closed.", true);
            return;

        if (it.currentBid == 0) {

            it.status = "Unsold";
showStatus(
                    "Auction closed with no bids.", false);

        } else {

            it.status = "Sold";

            showStatus(
                    "Winner: " + it.highestBidder
                            + " with a bid of " + it.currentBid,
                    false);
        }

        int index = itemList.getSelectedIndex();

        refreshList();
        itemList.select(index);

        saveData();

    // -----------------------------------------------------
    // Search Item
    // -----------------------------------------------------
    private void searchItem() {

        String key = searchField.getText()
                .trim().toLowerCase();
key.isEmpty()) {
            showStatus("Type a title to search.", true);
            return;
        }

        for (int i = 0; i < items.size(); i++) {

            Item it = items.get(i);

            if (it.title.        || it.category.toLowerCase().contains(key)) {

                itemList.select(i);

                fillFormFromSelection();

                showStatus(
                        "Found: " + it.title, false);

                return;
            }
        }

        showStatus("No matching item found.", true);
    }

    // -----------------------------------------------------
    // Bidder and Amount Summary
    // -----------------------------------------------------
    private void showBidSummary() {

        StringBuilder report = new StringBuilder();

        Set<String> allBidders = new HashSet<String>();

        int totalBids = 0;
        double totalAmount = 0;

        report.append(
                "AUCTION MANAGEMENT SYSTEM - BID SUMMARY\n");

        report.append(
                "========================================\n\n");

        Item selected = getSelectedItem();

        if (selected != null) {
            report.append(
                    "SELECTED ITEM DETAILS\n");

            report.append("Item: ")
                    .append(selected.title)
                    .append("\n");

            report.append("Unique bidders: ")
                    .append(selected.getBidderCount())
                    .append("\n");

            report.append("Number of bids: ")
                    .append(selected.bidHistory.size())
                    .append("\n");

            report.append("Total     .append(String.format(
                            "%.2f",
                            selected.getTotalBidAmount()))
                    .append("\n\n");

            report.append("BID HISTORY\n");
    report.append(
                    "----------------------------------------\n");

            if (selected.bidHistory.isEmpty()) {
                report.append(
                        "No bids have been placed.\n");

            } else {

                for (BidRecord bid : selected.bidHistory) {
        report.append("Bidder: ")
                            .append(bid.bidder)

                            .append(" | Amount: Rs. ")
                            .append(String.format(
                                    "%.2f", bid.amount))

                            .append(" | Date: ")
                            .append(bid.date)

                            .append(" | Day: ")
                            .append(bid.day)

                            .append(" | Time: ")
                            .append(bid.time)

                            .append("\n");
                }

            report.append("\n");

        } else {

            report.append(        "No item selected. Showing overall summary.\n\n");
        }

        // Overall summary
        for (Item it : items) {
(BidRecord bid : it.bidHistory) {

                allBidders.add(
                        bid.bidder.toLowerCase());

                totalBids++;

                totalAmount += bid.amount;
            }
        }

        report.append(TION SUMMARY\n");

        report.append(
                "----------------------------------------\n");

        report.append("Total auction items: ")
                .append(items.size())
                .append("\n");

        report.append("Total unique bidders: ")
                .append(allBidders.size())
                .append("\n");

        report.append("Total number of bids: ")
                .append(totalBids)
                .append("\n");

        report.append("Total amount of all bids: Rs. ")
                .append(String.format(
                        "%.2f", totalAmount))
                .append("\n");
        // Summary dialog
        Dialog dialog = new Dialog(
                this,
                "Bidder and Auction Summary",
                true);

        dialog.setLayout(
                new BorderLayout(8, 8));
 area = new TextArea(
                report.toString(),
                18,
                90,
                TextArea.SCROLLBARS_BOTH);

        area.setEditable(false);

        area.setFont(
                new Font("Monospaced", Font
        dialog.add(
                area,
                BorderLayout.CENTER);
        Button close = new Button("Close");

        close.addActionListener(
                e -> dialog.dispose());

        Panel panel = new Panel();

        panel.add(close);

        dialog.add(
                panel,
                BorderLayout.SOUTH);

        dialog.setSize(850, 450);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // -----------------------------------------------------
    // Helper Methods
    // -----------------------------------------------------
    private Item get
        int index = itemList.getSelectedIndex();

        if (index < 0 || index >= item    return null;

        return items.get(index);
    }

    private void fillFormFromSelection() {
Item it = getSelectedItem();

        if (it != null) {

            titleField.setText(it.title);
    categoryChoice.select(it.category);

            sellerField.setText(it.seller);

            priceField.setText(            String.valueOf(it.startPrice));
        }
    }

    private void refreshList() {
itemList.removeAll();

        for (Item it : items) {
            itemList.add(it.toString());
        }
    }

    // Seller name is NOT cleared here
    private void clearForm() {
        titleField.setText("");

        priceField.setText("");

        bidderField.setText("");

        bidField.setText("");
    }

    private void showStatus(
            String message, boolean error) {

        statusLabel.setForeground(
                error ? Color.RED : new Color(0, 70, 0));

        statusLabel.setText(message);
    }

    // -----------------------------------------------------
    // File Handling: Auction Item CSV
    // -----------------------------------------------------
    private void saveData() {

        try (PrintWriter out = new PrintWriter(FILE_NAME)) {

            for (Item it : items) {
                out.println(it.toCSV());
            }

        } catch (IOException e) {

            showStatus(
                    "Error saving auction file: "
                            + e.getMessage(),
                    true);

        saveBidData();
    }

    private void loadData() {

        File f = new File(FILE_NAME);

        if (!f.exists())
            return;

        items.clear();

        nextId = 1001;

        try (Scanner sc = new Scanner(f)) {

            while (sc.hasNextLine()) {

                Item it = Item.fromCSV(
                        sc.nextLine());

                if (it != null) {

                    items.add(it);

                    if (it.id >= nextId) {
                        nextId = it.id + 1;
                    }        }
            }

        } catch (IOException e) {

            showStatus(
                    "Error loading auction file: "
                            + e.getMessage(),
                    true);
        }
 ved seller name when loading data
        if (!items.isEmpty()) {

            savedSellerName = items.get(0).seller;

            sellerField.setText(savedSellerName);

            sellerField.setEditable(false);
        }

        refreshList();
    }
    // -----------------------------------------------------
    // File Handling: Separate CSV for Bid History
    // -----------------------------------------------------
    private void saveBidData() {

        try (PrintWriter out = new PrintWriter(BID_FILE)) {

            for (Item it : items) {

                for (BidRecord bid : it.bidHistory) {

                    out.println(
                            bid.toCSV(it.id));
                }
            }

        } catch (IOException e) {

            showStatus(
                    "Error saving bid file: "
                            + e.getMessage(),
                    true);
    }

    private void loadBidData() {

        for (Item it : items) {
            it.bidHistory.clear();
        }

        File f = new File(BID_FILE);

        if (!f.exists())
            return;

        try (Scanner sc = new Scanner(f)) {

            while (sc.hasNextLine()) {
    String[] p = sc.nextLine()
                        .split(",", -1);

                if (p.length < 6)
                    continue;

                try {

                    int itemId = Integer.parseInt(p[0]);

                    double amount = Double.parseDouble(p[2]);

                    for (Item it : items) {

                        if (it.id == itemId) {

                            it.bidHistory.add(
                                    new BidRecord(
                                            p[1],
                                            amount,
                                            p[3],
                                                      p[5]));

                            break;
                        }
                    }

                } catch (NumberFormatException ex) {
                    // Skip an invalid bid r
            }

        } catch (IOException e) {

            showStatus(
                    "Error loading bid file: "
                            + e.getMessage(),
                    true);
        }

    // -----------------------------------------------------
    // Program Entry Point
    // -----------------------------------------------------
    public static void main(String[] args) {

        new AuctionAWT();
    }
}