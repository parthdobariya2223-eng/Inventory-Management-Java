# Inventory Management System (Java Swing)

A desktop inventory app in 100% Java: Swing GUI, file storage (serialization), no database.

## Run it
Needs **JDK 11 or newer**.

* Windows: double-click `run.bat`
* Linux / macOS: `./run.sh`
* IntelliJ / Eclipse / NetBeans: create a project from this folder, mark the folder as the
  **source root**, and run `Main`. Make sure the **working directory is the project folder**
  (so the `data/` folder is found).

**Login:** `admin` / `admin123`

On the very first run a few sample products and transactions are created so the screens are not empty.
Delete the `data/` folder to start fresh.

## Project structure
| Folder | What is inside |
|---|---|
| `model/` | `Product`, `Transaction`, `User` (all `Serializable`) |
| `exception/` | `InvalidProductException`, `InsufficientStockException` |
| `repository/` | generic `Repository<T>` (ArrayList + file save/load), `ProductRepository` (adds a HashMap) |
| `service/` | `ProductService`, `InventoryService`, `TransactionService`, `UserService`, `BackgroundTasks`, `AppContext` |
| `gui/` | `LoginFrame`, `DashboardFrame`, `ProductFrame`, `StockFrame`, `TransactionFrame`, `ReportFrame` |
| `util/` | `Money` (₹ formatting with lakh grouping) |
| `data/` | `products.dat`, `transactions.dat`, `users.dat`, `backup/` (created automatically) |

## Where each syllabus topic is used (viva cheat-sheet)
| Topic | Where |
|---|---|
| Classes, encapsulation | `Product`, `Transaction`, `User` (private fields + getters/setters) |
| Custom exceptions | `InvalidProductException` (validation), `InsufficientStockException` (selling too much) |
| ArrayList | `Repository.data` |
| HashMap | `ProductRepository.map` (Product ID -> Product) |
| HashSet + TreeSet | `ProductService.getCategories()` (unique, then sorted) |
| Comparator | `ProductService.sort()` |
| Generics | `Repository<T extends Serializable>` used for Product, Transaction and User |
| Inheritance | `ProductRepository extends Repository<Product>`; all frames extend `JFrame` |
| File handling | `Repository.saveToFile / loadFromFile` (ObjectOutputStream / ObjectInputStream) |
| Inner class | `ProductFrame.AddButtonListener`, `BackgroundTasks.AutoSaver` |
| Event handling | `addActionListener` with lambdas and the inner class |
| Threads | `BackgroundTasks.AutoSaver` (Runnable + `Thread.sleep`) saves every 30 s |
| Executor framework | `BackgroundTasks` ScheduledExecutorService: low-stock check (30 s), backup (5 min) |
| synchronization | `synchronized` methods in `Repository`, `ProductService`; `synchronized (productService)` in `InventoryService` |
| Swing | JFrame, JPanel, JLabel, JTextField, JPasswordField, JButton, JComboBox, JTable, JScrollPane, JTabbedPane, JOptionPane |

## Notes
* Product IDs are text like `P101` (not numbers) and are stored in upper case; `p101` and `P101` are the same product.
* "Low stock" means `quantity <= minimum stock`.
* Quantity is changed only through Purchase / Sale (so every change appears in Transaction History).
  The Update Product button changes name, category, price, supplier and minimum stock.
