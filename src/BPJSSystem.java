import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

// Interface yang mendefinisikan operasi yang dapat dilakukan pada data peserta BPJS
interface BPJSOperations {
    void addDataPeserta();
    void tampilkanData();
    void updateData();
    void deleteData();
    void cariByNIK();
}

// Superclass BPJS yang menangani koneksi database dan login
class BPJS {
    protected static final String DB_URL = "jdbc:postgresql://localhost:5432/db_SistemBPJS"; // URL database ke postgresql
    protected static final String DB_USER = "postgres"; // Username database
    protected static final String DB_PASSWORD = "oliviagis"; // Password database

    protected Connection connection; // Koneksi ke database
    protected Scanner scanner; // Scanner untuk input pengguna

    // Constructor untuk menginisialisasi koneksi database
    public BPJS() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        scanner = new Scanner(System.in); // Inisialisasi scanner
    }

    // Metode untuk melakukan login
    protected void login() {
        String loginUsername = "java"; // Username untuk login
        String loginPassword = "TBPBO"; // Password untuk login
        
        boolean loggedIn = false; // Status login
        //Pada login diterapkan fungsi perulangan selama status login (loggedIn) masih false
        while (!loggedIn) {
            System.out.println("========Pendaftaran BPJS========");
            System.out.println("");
            System.out.println("=== Silahkan Login Terlebih Dahulu ===");
            System.out.print("Masukkan Username: ");
            String username = scanner.nextLine();
            System.out.print("Masukkan Password: ");
            String password = scanner.nextLine();

            // Memeriksa kecocokan username dan password dengan percabangan
            //Penggunaan manipulasi string and date yaitu equals pada username dan password
            if (username.equals(loginUsername) && password.equals(loginPassword)) {
                System.out.println("Login berhasil! Selamat Datang pada Sistem Pendaftaran BPJS\n");
                loggedIn = true; 
            } else {
                System.out.println("Username dan Password Salah. Silahkan Masukkan Kembali Username dan Password yang Benar.\n");
            }
        }
    }
}

// Subclass BPJSSystem yang mengimplementasikan interface BPJSOperations
public class BPJSSystem extends BPJS implements BPJSOperations {

    // Constructor untuk memanggil constructor superclass
    public BPJSSystem() {
        super();
    }

    // Metode untuk menambahkan data peserta BPJS (implementasi CRUD)
    @Override
    public void addDataPeserta() {
        System.out.println("=== Tambahkan Peserta BPJS ===");
        try {
            System.out.print("Masukkan NIK: ");
            String NIK = scanner.nextLine();

            System.out.print("Masukkan Nama Peserta: ");
            String nama_peserta = scanner.nextLine();

            // Menggunakan tanggal saat ini sebagai tanggal_pendaftaran
            Date tanggal_pendaftaran = new Date(System.currentTimeMillis());

            System.out.print("Masukkan Jumlah Tanggungan Anggota Keluarga: ");
            int jumlah_tanggungan = scanner.nextInt();

            System.out.print("Masukkan Pilihan Kelas BPJS (1, 2, 3): ");
            int kelas = scanner.nextInt();
            scanner.nextLine(); // Membersihkan newline

            // Menentukan total biaya berdasarkan kelas yang dipilih
            int total_biaya;
            switch (kelas) {
                case 1 -> total_biaya = 150000;
                case 2 -> total_biaya = 100000;
                case 3 -> total_biaya = 35000;
                default -> {
                    System.out.println("Pilihan Kelas Invalid.");
                    return;
                }
            }

            // operasi Matematika Menghitung total biaya tanggungan
            int totalBiayaTanggungan = total_biaya * jumlah_tanggungan;

            // Query untuk menambahkan data peserta ke database
            String query = "INSERT INTO peserta_bpjs (nik, nama_peserta, tanggal_pendaftaran, jumlah_tanggungan, kelas_bpjs, total_biaya) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, NIK);
            statement.setString(2, nama_peserta);
            statement.setDate(3, tanggal_pendaftaran); // Menggunakan tanggal saat ini
            statement.setInt(4, jumlah_tanggungan);
            statement.setInt(5, kelas);
            statement.setInt(6, totalBiayaTanggungan);

            // Menjalankan query untuk menambahkan data peserta
            statement.executeUpdate();
            System.out.println("Peserta Sukses Ditambahkan. Total Biaya Tanggungan: " + totalBiayaTanggungan + "\n");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // Metode untuk menampilkan semua data peserta BPJS
    @Override
    public void tampilkanData() {
        System.out.println("=== Menampilkan Semua Data Peserta BPJS ===");
        //penggunaan exception handling try and catch untuk menangani kesalahan
        try {
            // Query untuk mengambil seluruh data peserta BPJS
            String query = "SELECT nik, nama_peserta, tanggal_pendaftaran, jumlah_tanggungan, kelas_bpjs, total_biaya FROM peserta_bpjs";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Penggunaan TreeMap untuk mengurutkan berdasarkan NIK (key = NIK, value = data peserta)
            Map<String, String> pesertaData = new TreeMap<>();

            // Mengambil seluruh data dan menyimpannya dalam TreeMap
            while (resultSet.next()) {
                String nik = resultSet.getString("nik");
                String namaPeserta = resultSet.getString("nama_peserta");
                String tanggalPendaftaran = resultSet.getDate("tanggal_pendaftaran").toString();
                int jumlahTanggungan = resultSet.getInt("jumlah_tanggungan");
                int kelasBpjs = resultSet.getInt("kelas_bpjs");
                int totalBiaya = resultSet.getInt("total_biaya");

                // Format data peserta untuk ditampilkan
                String dataPeserta = String.format("Nama: %s | Tanggal Pendaftaran: %s | Jumlah Tanggungan: %d | Kelas BPJS : %d | Total biaya Tanggungan: %d",
                        namaPeserta, tanggalPendaftaran, jumlahTanggungan, kelasBpjs, totalBiaya);

                // Menyimpan data peserta dalam TreeMap berdasarkan NIK
                pesertaData.put(nik, dataPeserta);
            }

            // Menampilkan data peserta yang sudah diurutkan berdasarkan NIK dengan percabangan dan perulangan
            if (pesertaData.isEmpty()) {
                System.out.println("Data Tidak Ditemukan");
            } else {
                for (Map.Entry<String, String> entry : pesertaData.entrySet()) {
                    System.out.println("NIK: " + entry.getKey() + " | " + entry.getValue());
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // Metode untuk mencari data peserta berdasarkan NIK
    @Override
    public void cariByNIK() {
        System.out.println("=== Cari Data Berdasarkan NIK ===");
        //penggunaan exception handling try and catch untuk menangani kesalahan
        try {
            System.out.print("Masukkan NIK: ");
            String NIK = scanner.nextLine();

            // Query untuk mencari data berdasarkan NIK
            String query = "SELECT nama_peserta, tanggal_pendaftaran, jumlah_tanggungan, kelas_bpjs, total_biaya FROM peserta_bpjs WHERE nik = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, NIK);

            //menampilkan detail data yang dicari jika ditemukan 
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Data Ditemukan:");
                System.out.println("Name: " + resultSet.getString("nama_peserta"));
                System.out.println("Registration Date: " + resultSet.getDate("tanggal_pendaftaran"));
                System.out.println("Dependents: " + resultSet.getInt("jumlah_tanggungan"));
                System.out.println("BPJS Class: " + resultSet.getInt("kelas_bpjs"));
                System.out.println("Total Biaya Tanggungan: " + resultSet.getInt("total_biaya"));
            } else {
                System.out.println("Data dengan NIK tersebut Tidak Ditemukan: " + NIK);
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // Metode untuk memperbarui data peserta
    @Override
    public void updateData() {
        System.out.println("=== Update Data ===");
        try {
            System.out.print("Masukkan NIK: ");
            String NIK = scanner.nextLine();

            // Ambil tanggal_pendaftaran asli dari database
            String getDateQuery = "SELECT tanggal_pendaftaran FROM peserta_bpjs WHERE nik = ?";
            PreparedStatement getDateStatement = connection.prepareStatement(getDateQuery);
            getDateStatement.setString(1, NIK);
            ResultSet rs = getDateStatement.executeQuery();

            // Memeriksa apakah data peserta ada
            if (!rs.next()) {
                System.out.println("Data peserta tidak tersedia!\n");
                return;
            }

            // Mengambil tanggal pendaftaran asli
            Date originalTanggalPendaftaran = rs.getDate("tanggal_pendaftaran");

            // Mengambil input dari pengguna untuk memperbarui data
            System.out.print("Masukkan Nama: ");
            String nama_peserta = scanner.nextLine();

            System.out.print("Masukkan Jumlah Tanggungan: ");
            int jumlah_tanggungan = scanner.nextInt();

            System.out.print("Masukkan Pilihan Kelas (1, 2, 3): ");
            int kelas = scanner.nextInt();
            scanner.nextLine(); // Membersihkan newline

            // Menentukan total biaya berdasarkan kelas yang dipilih
            int total_biaya;
            switch (kelas) {
                case 1 -> total_biaya = 150000;
                case 2 -> total_biaya = 100000;
                case 3 -> total_biaya = 35000;
                default -> {
                    System.out.println("Kelas yang dipilih Invalid.");
                    return;
                }
            }

            // Menghitung total biaya tanggungan
            int totalBiayaTanggungan = total_biaya * jumlah_tanggungan;

            // Query untuk memperbarui data peserta tanpa mengubah tanggal pendaftaran asli
            String updateQuery = "UPDATE peserta_bpjs SET nama_peserta = ?, jumlah_tanggungan = ?, kelas_bpjs = ?, total_biaya = ? WHERE nik = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, nama_peserta);
            updateStatement.setInt(2, jumlah_tanggungan);
            updateStatement.setInt(3, kelas);
            updateStatement.setInt(4, totalBiayaTanggungan);
            updateStatement.setString(5, NIK);

            // Menjalankan query untuk memperbarui data
            int rowsUpdated = updateStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Data Berhasil di Update!\n");
            } else {
                System.out.println("Peserta Tidak Ditemukan!\n");
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // Metode untuk menghapus data peserta
    @Override
    public void deleteData() {
        System.out.println("=== Delete Data ===");
        try {
            System.out.print("Masukkan NIK: ");
            String NIK = scanner.nextLine();

            // Query untuk menghapus data peserta berdasarkan NIK
            String query = "DELETE FROM peserta_bpjs WHERE nik = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, NIK);

            // Menjalankan query untuk menghapus data
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Data Berhasil Dihapus!\n");
            } else {
                System.out.println("Data Peserta Tidak Ditemukan!\n");
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // Metode utama untuk menjalankan program
    public static void main(String[] args) {
        BPJSSystem system = new BPJSSystem();

        // Melakukan login
        system.login();

        // Menampilkan menu
        while (true) {
            System.out.println("=== BPJS System Menu ===");
            System.out.println("1. Tambah Peserta");
            System.out.println("2. Tampilkan Semua Data Peserta");
            System.out.println("3. Update Data Peserta");
            System.out.println("4. Hapus Data Peserta");
            System.out.println("5. Cari Data Peserta");
            System.out.println("0. Keluar");
            System.out.print("Select an option: ");
            int choice = system.scanner.nextInt();
            system.scanner.nextLine(); // Membersihkan newline

            // Menggunakan switch-case untuk memilih opsi menu
            switch (choice) {
                case 1:
                    system.addDataPeserta();
                    break;
                case 2:
                    system.tampilkanData();
                    break;
                case 3:
                    system.updateData();
                    break;
                case 4:
                    system.deleteData();
                    break;
                case 5:
                    system.cariByNIK();
                    break;
                case 0:
                    System.out.println("Keluar dari Sistem...");
                    return; // Keluar dari program
                default:
                    System.out.println("Pilihan Invalid");
            }
        }
    }
}