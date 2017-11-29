package dbZ2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.pool.OracleDataSource;

public class JDBC_Verwaltung
{

	static Connection con = null;

	public static ResultSet executeSQL(String sql, String tab) throws SQLException
		{
			Statement Stmt;

			ResultSet tempRS;
			// Erzeugen eines Statements aus der DB-Verbindung
			Stmt = con.createStatement();

			String SQL = sql + tab;

			// SQL-Anweisung ausf�hren und Ergebnis in ein ResultSet schreiben
			tempRS = Stmt.executeQuery(SQL);
			return tempRS;
		}

	public static void metaHandling(ResultSet RS) throws SQLException
		{
			ResultSetMetaData rsmd = RS.getMetaData();
			System.out.println("\n\n");
			String names = "";
			String data = "";

			for (int i = 1; i <= rsmd.getColumnCount(); i++)
				{

					names += rsmd.getColumnName(i) + "\t\t\t";
				}
			System.out.println(names);
			while (RS.next())
				{
					data = "";
					for (int i = 1; i <= rsmd.getColumnCount(); i++)
						{
							data += RS.getString(i) + "\t\t\t";
						}

					System.out.println(data);
				}
			RS.close();
		}

	public static Connection connect() throws IOException, SQLException
		{
			String treiber;
			OracleDataSource ods = new OracleDataSource();

			treiber = "oracle.jdbc.driver.OracleDriver";
			Connection dbConnection = null;
			String uName;
			String pW;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			//System.out.println("Please enter username: ");
			//uName = in.readLine();
		//	System.out.println("Please enter password: ");
		//	pW = in.readLine();

			// Treiber laden

			try
				{

					Class.forName(treiber).newInstance();
				} catch (Exception e)
				{
					System.out.println("Fehler beim laden des Treibers: " + e.getMessage());
				}

			// Erstellung Datenbank-Verbindungsinstanz
			try
				{
					ods.setURL("jdbc:oracle:thin:dbprak39/salamistulle@schelling.nt.fh-koeln.de:1521:xe");
					//ods.setURL("jdbc:oracle:thin:" + uName + "/" + pW + "@schelling.nt.fh-koeln.de:1521:xe");
					dbConnection = ods.getConnection();
				} catch (SQLException e)
				{
					System.out.println("Fehler beim Verbindungsaufbau zur Datenbank!");
					System.out.println(e.getMessage());
				}
			pW = "";
			uName = "";
			return dbConnection;
		}

	public static void main(String[] args) throws IOException
		{

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			// Datenbankverbindung erstellen mit Hilfe der "connect()"-Methode
			try
				{
					con = connect();

					// JDBC Objekte
					Statement Stmt = con.createStatement();
					ResultSet RS;
					String SQL;

					//MENUE
					int choice = 0;
					do
						{
							System.out.println("Willkommen im Menue:");
							System.out.println("(1)Neue Datens�tze importieren mit JDBC INSERT");
							System.out.println("(2)Datens�tze abrufen");
							System.out.println("(3)Artikel Auskunft, nach ArtikelNr");
							System.out.println("(4)Programm beenden");

							try
								{
									choice = Integer.parseInt(in.readLine());

								} catch (Exception e)
								{
									System.out.println(e.getMessage());
								}

							switch (choice)
								{
								case 1:
									SQL = ("INSERT INTO ARTIKEL VALUES (null, 'Pflaumen',1,'5,99','0,19', TO_DATE ('01-02-2017','DD-MM-YYYY'))");

									int a = Stmt.executeUpdate(SQL);
									if (a == 1)
										{
											System.out.println("Update erfolgreich!");
										} else
										{
											System.out.println("Update fehlgeschlagen!");
										}
									break;

								case 2:
									//SUB MEN�

									do
										{
											System.out.println("Welche Tabelle soll geladen werden?:");
											System.out.println("(1)ARTIKEL");
											System.out.println("(2)LAGER");
											System.out.println("(3)KUNDEN");
											System.out.println("(0)QUIT");

											try
												{
													choice = Integer.parseInt(in.readLine());

												} catch (Exception e)
												{
													System.out.println(e.getMessage());
												}

											String sALL = "SELECT * FROM ";
											switch (choice)

												{
												case 1:
													System.out.println("\n\n");
													RS = executeSQL(sALL, "ARTIKEL");
													metaHandling(RS);
													System.out.println("\n\n");
													break;

												case 2:
													System.out.println("\n\n");
													RS = executeSQL(sALL, "LAGER");
													metaHandling(RS);
													System.out.println("\n\n");
													break;

												case 3:
													System.out.println("\n\n");
													RS = executeSQL(sALL, "KUNDE");
													metaHandling(RS);
													System.out.println("\n\n");
													break;
												}//SWITCH END

										} while (choice != 0);
									// SUB MEN� END
									choice=1;
									break;

								//ARTIKEL SUCHE NACH ARTNR
								case 3:
									System.out.println("\n\n");
									System.out.println("Bitte Artikel Nr eingeben: ");
									String artNr = in.readLine();
									RS = executeSQL("SELECT ARTIKEL.ARTNR, ARTIKEL.ARTBEZ, ARTIKEL.MGE, ARTIKEL.PREIS, ARTIKEL.STEU, TO_CHAR(ARTIKEL.EDAT,'DD-MM-YYYY') EDAT , LAGERBESTAND.BSTNR, LAGERBESTAND.LNR, LAGERBESTAND.MENGE, LAGER.LORT, LAGER.LPLZ FROM ARTIKEL,LAGERBESTAND,LAGER ",("WHERE ARTIKEL.ARTNR=" + artNr + " AND LAGERBESTAND.ARTNR=" + artNr + " AND LAGER.LNR= LAGERBESTAND.LNR"));
									metaHandling(RS);
								
									RS = executeSQL("SELECT SUM (LAGERBESTAND.MENGE) Gesamtbestand  FROM LAGERBESTAND"," WHERE LAGERBESTAND.ARTNR = "+artNr);
									metaHandling(RS);

									break;

								case 4:

								}
						} while (choice != 0);

					// MENUE END

					// SQL Exception abfangen
				} catch (SQLException e)
				{
					System.out.println(e.getMessage());
					System.out.println("SQL Exception wurde geworfen!");
				}

		}

}