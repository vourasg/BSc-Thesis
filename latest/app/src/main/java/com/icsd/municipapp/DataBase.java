package com.icsd.municipapp;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

class DataBase extends SQLiteOpenHelper
{

    private static final String dbName="MunicipAppDB";



    DataBase(Context context) {
        super(context, dbName, null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        System.out.println("DB on create called");
        db.execSQL(getRegionTable());
        db.execSQL(getLocalityTable());

        db.execSQL(getRegionData());
        db.execSQL(getLocalityData());

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(getDelete());
        onCreate(db);
    }

    public String[] getRegions() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select region from Regions ", null );
        ArrayList<String>list=new ArrayList<>();
        String[] regions;
        if (cursor.moveToFirst())
        {
            do{
                String data = cursor.getString(cursor.getColumnIndex("region"));
                list.add(data);
            }while(cursor.moveToNext());
            regions=new String[list.size()];
            for(int i=0; i<list.size(); i++)
                regions[i]=list.get(i);
            cursor.close();
            return regions;
        }
        cursor.close();
        return new String[0];
    }

    public String[] getLocalities(String region) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select Locality.locality from Locality,Regions where Locality.region_id=Regions.id AND Regions.region='"+region+"';", null );
        ArrayList<String>list=new ArrayList<>();
        String[] localities;
        if (cursor.moveToFirst())
        {
            do{
                String data = cursor.getString(cursor.getColumnIndex("locality"));
                list.add(data);
            }while(cursor.moveToNext());
            localities=new String[list.size()];
            for(int i=0; i<list.size(); i++)
                localities[i]=list.get(i);
            cursor.close();
            return localities;
        }
        cursor.close();
        return new String[0];
    }


    private String getDelete(){

        return "DROP TABLE IF EXISTS Regions;\nDROP TABLE IF EXISTS Locality";
    }

    private String getRegionTable()    {
        return "CREATE TABLE IF NOT EXISTS Regions (`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,`region`	TEXT NOT NULL);";
    }

    private String getLocalityTable()    {
        return "CREATE TABLE IF NOT EXISTS Locality (`id`	INTEGER PRIMARY KEY AUTOINCREMENT,`locality`	TEXT NOT NULL,`region_id`	INTEGER NOT NULL);";
    }

    private String getRegionData(){
        return "INSERT INTO `Regions` (id,region) VALUES (1,'Αιτωλοακαρνανιας'),\n" +
                " (2,'Αργολιδας'),\n" +
                " (3,'Αρκαδιας'),\n" +
                " (4,'Αρτας'),\n" +
                " (5,'Αττικης'),\n" +
                " (6,'Αχαϊας'),\n" +
                " (7,'Βοιωτιας'),\n" +
                " (8,'Γρεβενων'),\n" +
                " (9,'Δραμας'),\n" +
                " (10,'Δωδεκανησου'),\n" +
                " (11,'Εβρου'),\n" +
                " (12,'Ευβοιας'),\n" +
                " (13,'Ευρυτανιας'),\n" +
                " (14,'Ζακυνθου'),\n" +
                " (15,'Ηλειας'),\n" +
                " (16,'Ημαθιας'),\n" +
                " (17,'Ηρακλειου'),\n" +
                " (18,'Θεσπρωτιας'),\n" +
                " (19,'Θεσσαλονικης'),\n" +
                " (20,'Ιωαννινων'),\n" +
                " (21,'Καβαλας'),\n" +
                " (22,'Καρδιτσας'),\n" +
                " (23,'Καστοριας'),\n" +
                " (24,'Κερκυρας'),\n" +
                " (25,'Κεφαλληνιας'),\n" +
                " (26,'Κιλκις'),\n" +
                " (27,'Κοζανης'),\n" +
                " (28,'Κορινθιας'),\n" +
                " (29,'Κυκλαδων'),\n" +
                " (30,'Λακωνιας'),\n" +
                " (31,'Λαρισας'),\n" +
                " (32,'Λασιθιου'),\n" +
                " (33,'Λεσβου'),\n" +
                " (34,'Λευκαδας'),\n" +
                " (35,'Μαγνησιας'),\n" +
                " (36,'Μεσσηνιας'),\n" +
                " (37,'Ξανθης'),\n" +
                " (38,'Πελλας'),\n" +
                " (39,'Πιεριας'),\n" +
                " (40,'Πρεβεζας'),\n" +
                " (41,'Ρεθυμνου'),\n" +
                " (42,'Ροδοπης'),\n" +
                " (43,'Σαμου'),\n" +
                " (44,'Σερρων'),\n" +
                " (45,'Τρικαλων'),\n" +
                " (46,'Φθιωτιδας'),\n" +
                " (47,'Φλωρινας'),\n" +
                " (48,'Φωκιδας'),\n" +
                " (49,'Χαλκιδικης'),\n" +
                " (50,'Χανιων'),\n" +
                " (51,'Χιου');";
    }

    private String getLocalityData(){
        return "INSERT INTO `Locality` (id,locality,region_id) VALUES (1,'Αλεξανδρούπολη',11),\n" +
                " (2,'Διδυμότειχο',11),\n" +
                " (3,'Ορεστιάδα',11),\n" +
                " (4,'Σαμοθράκη',11),\n" +
                " (5,'Σουφλί',11),\n" +
                " (6,'Αρριανά',42),\n" +
                " (7,'Ίασμος',42),\n" +
                " (8,'Κομοτηνή',42),\n" +
                " (9,'Μαρώνεια  - Σάπες',42),\n" +
                " (10,'Άβδηρα',37),\n" +
                " (11,'Μύκη',37),\n" +
                " (12,'Ξάνθη',37),\n" +
                " (13,'Τοπείρα',37),\n" +
                " (14,'Καβάλα',21),\n" +
                " (15,'Θάσος',21),\n" +
                " (16,'Νέστος',21),\n" +
                " (17,'Παγγαίο',21),\n" +
                " (18,'Δράμα',9),\n" +
                " (19,'Δοξάτο',9),\n" +
                " (20,'Κάτω Νευροκόπι',9),\n" +
                " (21,'Παρανέστι',9),\n" +
                " (22,'Προσοτσάνη',9),\n" +
                " (23,'Αμπελόκηποι - Μενεμένη',19),\n" +
                " (24,'Βόλβη',19),\n" +
                " (25,'Δέλτα',19),\n" +
                " (26,'Θερμαϊκός',19),\n" +
                " (27,'Θέρμη',19),\n" +
                " (28,'Θεσσαλονίκη',19),\n" +
                " (29,'Καλαμαριά',19),\n" +
                " (30,'Κορδελιό - Εύοσμος',19),\n" +
                " (31,'Λαγκαδάς',19),\n" +
                " (32,'Νεάπολη - Συκές',19),\n" +
                " (33,'Παύλου Μελά',19),\n" +
                " (34,'Πυλαία - Χορτιάτης',19),\n" +
                " (35,'Χαλκηδόνα',19),\n" +
                " (36,'Ωραιόκαστρο',19),\n" +
                " (37,'Κιλκίς',16),\n" +
                " (38,'Παιονία',16),\n" +
                " (39,'Πέλλας',38),\n" +
                " (40,'Σκύδρα',38),\n" +
                " (41,'Έδεσσα',38),\n" +
                " (42,'Αλμωπίας',38),\n" +
                " (43,'Βέροιας',16),\n" +
                " (44,'Αλεξάνδρειας',16),\n" +
                " (45,'Ηρωικής Πόλεως Νάουσας',16),\n" +
                " (46,'Κατερίνης',39),\n" +
                " (47,'Δίου - Ολύμπου',39),\n" +
                " (48,'Πύδνας - Κολυνδρού',39),\n" +
                " (49,'Σερρών',44),\n" +
                " (50,'Αμφίπολης',44),\n" +
                " (51,'Βισαλτίας',44),\n" +
                " (52,'Εμμανουήλ Παππά',44),\n" +
                " (53,'Ηρακλείας',44),\n" +
                " (54,'Νέα Ζίχνης',44),\n" +
                " (55,'Αρειστοτέλη',49),\n" +
                " (56,'Κασσάνδρας',49),\n" +
                " (57,'Νέας Προποντίδας',49),\n" +
                " (58,'Πολυγύρου',49),\n" +
                " (59,'Σιθωνίας',49),\n" +
                " (60,'Γρεβενών',8),\n" +
                " (61,'Δεσκάτης',8),\n" +
                " (62,'Καστοριάς',23),\n" +
                " (63,'Νεστορίου',23),\n" +
                " (64,'Άργους Ορεστικού',23),\n" +
                " (65,'Κοζάνης',27),\n" +
                " (66,'Βοίου',27),\n" +
                " (67,'Εορδαίας',27),\n" +
                " (68,'Σερβίων - Βελβεντού',27),\n" +
                " (69,'Φλόρινας',47),\n" +
                " (70,'Πρεσπών',47),\n" +
                " (71,'Αμυνταίου',47),\n" +
                " (72,'Αρταίων',4),\n" +
                " (73,'Γεωργίου Καραϊσκάκη',4),\n" +
                " (74,'Κεντρικών Τζουμέρκων',4),\n" +
                " (75,'Νικολάου Σκουφά',4),\n" +
                " (76,'Ηγουμενίτσας',18),\n" +
                " (77,'Σουλίου',18),\n" +
                " (78,'Φιλιατών',18),\n" +
                " (79,'Ιωαννιτών',20),\n" +
                " (80,'Βορείων Τζουμέρκων',20),\n" +
                " (81,'Δωδώνης',20),\n" +
                " (82,'Ζαγορίου',20),\n" +
                " (83,'Ζίτσας',20),\n" +
                " (84,'Κόντισας',20),\n" +
                " (85,'Μετσόβου',20),\n" +
                " (86,'Πωγωνίου',20),\n" +
                " (87,'Πρέβεζας',40),\n" +
                " (88,'Ζηρού',40),\n" +
                " (89,'Πάργας',40),\n" +
                " (90,'Καρδίτσας',22),\n" +
                " (91,'Αργιθέας',22),\n" +
                " (92,'Λίμνης Πλαστήρα',22),\n" +
                " (93,'Μουζακίου',22),\n" +
                " (94,'Παλαμά',22),\n" +
                " (95,'Σοφάδων',22),\n" +
                " (96,'Λαρισαίων',31),\n" +
                " (97,'Αγιάς',31),\n" +
                " (98,'Ελασσόας',31),\n" +
                " (99,'Κιλελέρ',31),\n" +
                " (100,'Τεμπών',31),\n" +
                " (101,'Τυρνάβου',31),\n" +
                " (102,'Φαρσάλων',31),\n" +
                " (103,'Αλμυρού',35),\n" +
                " (104,'Βόλου',35),\n" +
                " (105,'Ζαγοράς - Μουρεσίου',35),\n" +
                " (106,'Νοτίου Πηλίου',35),\n" +
                " (107,'Ρήγα Φερραίου',35),\n" +
                " (108,'Αλοννήσου',35),\n" +
                " (109,'Σκιάθου',35),\n" +
                " (110,'Σκοπέλου',35),\n" +
                " (111,'Τρικκαίων',45),\n" +
                " (112,'Καλαμπάκας',45),\n" +
                " (113,'Πύλης',45),\n" +
                " (114,'Φαρκαδόνας',45),\n" +
                " (115,'Ζακύνθου',14),\n" +
                " (116,'Κέρκυρας',24),\n" +
                " (117,'Παξών',24),\n" +
                " (118,'Κεφαλονιάς',25),\n" +
                " (119,'Ιθάκης',25),\n" +
                " (120,'Λευκάδος',34),\n" +
                " (121,'Μεγανησίου',34),\n" +
                " (122,'Αμφιλοχίας',1),\n" +
                " (123,'Αγρινίου',1),\n" +
                " (124,'Άκτιου - Βόνιτσας',1),\n" +
                " (125,'Θέρμου',1),\n" +
                " (126,'Ιεράς Πόλης Μεσολογγίου',1),\n" +
                " (127,'Ναυπακτίας',1),\n" +
                " (128,'Ξηρομέρου',1),\n" +
                " (129,'Αιγιαλείας',6),\n" +
                " (130,'Δυτικής Αχαϊας',6),\n" +
                " (131,'Ερυμάνθου',6),\n" +
                " (132,'Καλαβρύτων',6),\n" +
                " (133,'Πατρέων',6),\n" +
                " (134,'Ήλιδας',15),\n" +
                " (135,'Πηνειού',15),\n" +
                " (136,'Πύργου',15),\n" +
                " (137,'Ανδραβίδας - Κυλλήνης',15),\n" +
                " (138,'Ανδρίτσαινας - Κρεστένων',15),\n" +
                " (139,'Αρχαίας Ολυμπίας',15),\n" +
                " (140,'Ζαχάρως',15),\n" +
                " (141,'Αλιάρτου - Θεσπιαίων',7),\n" +
                " (142,'Διστόμου - Αράχοβας - Αντίκυρας',7),\n" +
                " (143,'Θηβαίων',7),\n" +
                " (144,'Λεβαδέων',7),\n" +
                " (145,'Ορχομενού',7),\n" +
                " (146,'Τανάγρας',7),\n" +
                " (147,'Διρφύων - Μεσσαπίων',12),\n" +
                " (148,'Ερέτριας',12),\n" +
                " (149,'Ιστιαίας - Αιδηψού',12),\n" +
                " (150,'Καρύστου',12),\n" +
                " (151,'Κύμης - Αλιβερίου',12),\n" +
                " (152,'Μαντουδίου - Λίμνης - Αγίας Άννας',12),\n" +
                " (153,'Σκύρου',12),\n" +
                " (154,'Χαλκιδέων',12),\n" +
                " (155,'Καρπενησίου',13),\n" +
                " (156,'Αγράφων',13),\n" +
                " (157,'Μώλου - Αγίου Κωνσταντίνου',46),\n" +
                " (158,'Αμφίκλειας - Ελάτειας',46),\n" +
                " (159,'Λοκρών',46),\n" +
                " (160,'Δομοκού',46),\n" +
                " (161,'Λαμιέων',46),\n" +
                " (162,'Μακρακώμης',46),\n" +
                " (163,'Στυλίδος',46),\n" +
                " (164,'Δελφών',48),\n" +
                " (165,'Δωρίδος',48),\n" +
                " (166,'Αγίας Παρασκευής',5),\n" +
                " (167,'Αμαρουσίου',5),\n" +
                " (168,'Βριλησσίων',5),\n" +
                " (169,'Ηρακλείου',5),\n" +
                " (170,'Κηφισιάς',5),\n" +
                " (171,'Λυκόβρυσης - Πεύκης',5),\n" +
                " (172,'Μεταμορφώσεος',5),\n" +
                " (173,'Νέας Ιωνίας',5),\n" +
                " (174,'Παπάγου - Χολαργού',5),\n" +
                " (175,'Πεντέλης',5),\n" +
                " (176,'Φιλοθέης - Ψυχικού',5),\n" +
                " (177,'Χαλανδρίου',5),\n" +
                " (178,'Αγίας Βαρβάρας',5),\n" +
                " (179,'Αγίων Αναργύρων - Καματερού',5),\n" +
                " (180,'Αιγάλεω',5),\n" +
                " (181,'Ιλίου',5),\n" +
                " (182,'Περιστερίου',5),\n" +
                " (183,'Πετρούπολης',5),\n" +
                " (184,'Χαϊδαρίου',5),\n" +
                " (185,'Αθηναίων',5),\n" +
                " (186,'Βύρωνος',5),\n" +
                " (187,'Γαλατσίου',5),\n" +
                " (188,'Δάφνης - Υμηττού',5),\n" +
                " (189,'Ζωγράφου',5),\n" +
                " (190,'Ηλιουπόλεος',5),\n" +
                " (191,'Καισαριανής',5),\n" +
                " (192,'Φιλαδέλφειας - Χαλκηδόνας',5),\n" +
                " (193,'Αγίου Δημιτρίου',5),\n" +
                " (194,'Αλίμου',5),\n" +
                " (195,'Γλυφάδας',5),\n" +
                " (196,'Ελληνικού - Αργυρούπολης',5),\n" +
                " (197,'Καλλιθέας',5),\n" +
                " (198,'Μοσχάτου - Ταύρου',5),\n" +
                " (199,'Νέας Σμύρνης',5),\n" +
                " (200,'Παλαιού Φαλήρου',5),\n" +
                " (201,'Αχαρνών',5),\n" +
                " (202,'Βάρης - Βούλας - Βουλιαγμένης',5),\n" +
                " (203,'Διονύσου',5),\n" +
                " (204,'Κρωπίας',5),\n" +
                " (205,'Λαυρεωτικής',5),\n" +
                " (206,'Μαραθώνος',5),\n" +
                " (207,'Μαρκοπούλου Μεσογαίας',5),\n" +
                " (208,'Παιανίας',5),\n" +
                " (209,'Παλλήνης',5),\n" +
                " (210,'Ραφήνας - Πικερμίου',5),\n" +
                " (211,'Σαρωνικού',5),\n" +
                " (212,'Σπάτων - Αρτέμιδος',5),\n" +
                " (213,'Ωρωπού',5),\n" +
                " (214,'Ασπροπύργου',5),\n" +
                " (215,'Ελευσίνας',5),\n" +
                " (216,'Μάνδρας - Ειδυλλίας',5),\n" +
                " (217,'Μεγαρέων',5),\n" +
                " (218,'Φυλής',5),\n" +
                " (219,'Κερατσινίου - Δραπετσώνας',5),\n" +
                " (220,'Κορυδαλλού',5),\n" +
                " (221,'Νίκαιας - Αγίου Ιωάννου Ρέντη',5),\n" +
                " (222,'Πειραιώς',5),\n" +
                " (223,'Περάματος',5),\n" +
                " (224,'Αγκιστρίου',5),\n" +
                " (225,'Αίγινας',5),\n" +
                " (226,'Κυθήρων',5),\n" +
                " (227,'Πόρου',5),\n" +
                " (228,'Σαλαμίνας',5),\n" +
                " (229,'Σπετσών',5),\n" +
                " (230,'Τροιζηνίας - Μεθάνων',5),\n" +
                " (231,'Ύδρας',5),\n" +
                " (232,'Άργους - Μυκηνών',2),\n" +
                " (233,'Επιδαύρου',2),\n" +
                " (234,'Ερμιονίας',2),\n" +
                " (235,'Ναυπλοιέων',2),\n" +
                " (236,'Βόρειας Κυνουρίας',3),\n" +
                " (237,'Γόρτυνος',3),\n" +
                " (238,'Μεγαλόπολης',3),\n" +
                " (239,'Νότιας Κυνουρίας',3),\n" +
                " (240,'Τρίπολης',3),\n" +
                " (241,'Βέλου - Βόχας',28),\n" +
                " (242,'Κορινθίων',28),\n" +
                " (243,'Λουτρακίου - Περαχώρας - Αγ. Θεοδώρων',28),\n" +
                " (244,'Νεμέας',28),\n" +
                " (245,'Ξυλοκάστρου - Ευρωστίνης',28),\n" +
                " (246,'Σικυωνίων',28),\n" +
                " (247,'Ανατολικής Μάνης',30),\n" +
                " (248,'Ελαφονήσου',30),\n" +
                " (249,'Ευρώτα',30),\n" +
                " (250,'Μονεμβασίας',30),\n" +
                " (251,'Σπάρτης',30),\n" +
                " (252,'Δυτικής Μάνης',36),\n" +
                " (253,'Καλαμάτας',36),\n" +
                " (254,'Μεσσήνης',36),\n" +
                " (255,'Οιχαλίας',36),\n" +
                " (256,'Πύλου - Νέστορος',36),\n" +
                " (257,'Τριφυλίας',36),\n" +
                " (258,'Λέσβου',33),\n" +
                " (259,'Λήμνου',33),\n" +
                " (260,'Αγίου Ευστρατίου',33),\n" +
                " (261,'Σάμου',43),\n" +
                " (262,'Ικαρίας',43),\n" +
                " (263,'Φούρνων - Κορσέων',43),\n" +
                " (264,'Χίου',51),\n" +
                " (265,'Ψαρών',51),\n" +
                " (266,'Οινουσσών',51),\n" +
                " (267,'Άνδρου',29),\n" +
                " (268,'Ανάφης',29),\n" +
                " (269,'Θήρας',29),\n" +
                " (270,'Ιητών',29),\n" +
                " (271,'Σικίνου',29),\n" +
                " (272,'Φολεγάνδρου',29),\n" +
                " (273,'Κέας',29),\n" +
                " (274,'Κύθνου',29),\n" +
                " (275,'Κιμώλου',29),\n" +
                " (276,'Μήλου',29),\n" +
                " (277,'Σερίφου',29),\n" +
                " (278,'Σίφνου',29),\n" +
                " (279,'Μυκόνου',29),\n" +
                " (280,'Αμοργού',29),\n" +
                " (281,'Ναξου & Μικρών Κυκλάδων',29),\n" +
                " (282,'Αντιπάρου',29),\n" +
                " (283,'Πάρου',29),\n" +
                " (284,'Σύρου - Ερμούπολης ',29),\n" +
                " (285,'Τήνου',29),\n" +
                " (286,'Αγαθονησίου',10),\n" +
                " (287,'Αστυπαλαίας',10),\n" +
                " (288,'Καλυμνίων',10),\n" +
                " (289,'Καρπάθου',10),\n" +
                " (290,'Κάσου',10),\n" +
                " (291,'Λειψών',10),\n" +
                " (292,'Λέρου',10),\n" +
                " (293,'Πάτμου',10),\n" +
                " (294,'Κω',10),\n" +
                " (295,'Νισύρου',10),\n" +
                " (296,'Μεγίστης',10),\n" +
                " (297,'Ρόδου',10),\n" +
                " (298,'Σύμης',10),\n" +
                " (299,'Τήλου',10),\n" +
                " (300,'Χάλκης',10),\n" +
                " (301,'Αρχανών - Αστερουσίων',17),\n" +
                " (302,'Βιάννου',17),\n" +
                " (303,'Γόρτυνας',17),\n" +
                " (304,'Ηρακλείου Κρήτης',17),\n" +
                " (305,'Μαλεβιζίου',17),\n" +
                " (306,'Μινώα Πεδιάδας',17),\n" +
                " (307,'Φαιστού',17),\n" +
                " (308,'Χερσονήσου',17),\n" +
                " (309,'Αγίου Νικολάου',32),\n" +
                " (310,'Ιεράπερτας',32),\n" +
                " (311,'Οροπεδίου Λασιθίου',32),\n" +
                " (312,'Σητείας',32),\n" +
                " (313,'Αγίου Βασιλείου',41),\n" +
                " (314,'Αμαρίου',41),\n" +
                " (315,'Ανωγείων',41),\n" +
                " (316,'Μηλοποτάμου',41),\n" +
                " (317,'Ρεθύμνης',41),\n" +
                " (318,'Χανίων',50),\n" +
                " (319,'Αποκορώνου',50),\n" +
                " (320,'Γαύδου',50),\n" +
                " (321,'Καντάνου - Σέλινου',50),\n" +
                " (322,'Κισσάμου',50),\n" +
                " (323,'Πλατανιά',50),\n" +
                " (324,'Σφακίων',50);";
    }
}