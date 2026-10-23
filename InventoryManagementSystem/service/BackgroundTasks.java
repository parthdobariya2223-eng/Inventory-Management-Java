package service;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Background work:
 *  1. A Thread (Runnable + sleep) that auto-saves data every 30 seconds.
 *  2. An ExecutorService that checks for low stock every 30 seconds
 *     and makes a backup copy of the data files every 5 minutes.
 */
public class BackgroundTasks {
    private final AppContext ctx;
    private volatile boolean running = false;
    private Thread autoSaveThread;
    private ScheduledExecutorService executor;
    private volatile Consumer<Integer> lowStockListener;

    public BackgroundTasks(AppContext ctx) {
        this.ctx = ctx;
    }

    /** The Dashboard registers here to be told about the low-stock count. */
    public void setLowStockListener(Consumer<Integer> listener) {
        this.lowStockListener = listener;
    }

    // Runnable implemented as an inner class
    private class AutoSaver implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(30_000);
                } catch (InterruptedException e) {
                    return;
                }
                ctx.saveAll();
            }
        }
    }

    public void start() {
        running = true;

        autoSaveThread = new Thread(new AutoSaver(), "AutoSaveThread");
        autoSaveThread.setDaemon(true);
        autoSaveThread.start();

        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "BackgroundExecutor");
            t.setDaemon(true);
            return t;
        });
        executor.scheduleAtFixedRate(this::checkLowStock, 5, 30, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(this::backup, 1, 5, TimeUnit.MINUTES);
    }

    private void checkLowStock() {
        try {
            final int count = ctx.inventoryService.getLowStockProducts().size();
            final Consumer<Integer> listener = lowStockListener;
            if (listener != null) {
                SwingUtilities.invokeLater(() -> listener.accept(count));   // update GUI safely
            }
        } catch (Exception e) {
            System.out.println("Low stock check failed: " + e.getMessage());
        }
    }

    private void backup() {
        try {
            Files.createDirectories(Paths.get("data/backup"));
            copyIfExists(ProductService.FILE, "data/backup/products_backup.dat");
            copyIfExists(TransactionService.FILE, "data/backup/transactions_backup.dat");
        } catch (IOException e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }

    private void copyIfExists(String from, String to) throws IOException {
        Path source = Paths.get(from);
        if (Files.exists(source)) {
            Files.copy(source, Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public void stop() {
        running = false;
        if (autoSaveThread != null) autoSaveThread.interrupt();
        if (executor != null) executor.shutdownNow();
    }
}
