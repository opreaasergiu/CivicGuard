# Civic-Guard

## Echipa:
- [Rosca Alexandru](https://github.com/RoscaAlexandru775)
- [Oprea Sergiu](https://github.com/opreaasergiu)
- [Pislaru Alin](https://github.com/pislaru-alin)
- [Iordache Ovidiu](https://github.com/OvidiuIordache1)
- [Zamora Ionut](https://github.com/zzzamoraaa)

## Demo:
Demo-ul aplicatiei poate fi gasit [aici](https://youtu.be/J_-CoKe3upU)
 
Link download apk: [dropbox](https://www.dropbox.com/s/59zn40mz3tcz1cv/CivicGuard.apk?dl=0)

## Description:
Proiectul este o aplicatie in Android folosind Kotlin.
1) Ce face aplicatia?
  Aplicatia faciliteaza reclamarea problemelor ce pot apărea în viața de zi cu zi într-un oras. Un user poate semnala astfel de probleme (de exemplu semafoare defecte, gunoi aruncat în spatiul public etc.), iar o institutie (Firma apa canalizare / Primarie) poate modifica status-ul unei reclamatii (solved / unsolved). De asemenea, un user poate sa vada si reclamatiile facute de alti utilizatori, precum si locatia in care acestea au fost facute.
2) Cine o sa o folosească?
  Aplicatia poate fi folosita de orice utilizator de telefon Android (versiune minimă 4.6) care vrea să se implice activ în rezolvarea problemelor orasului sau.
3) De ce o să o o folosească?
  Pentru a semnala neregulile din oras.
4) Când o să o folosească?
  De cate ori observa probleme pe care le poate trimite către autoritățile competente spre solutionare.

## User Stories:
1. Ca un vizitator vreau să-mi pot crea un cont pentru a folosi aplicația. 
2. Ca un vizitator vreau să pot să mă uit prin alte reclamații. 
3. Ca un utilizator vreau să am o opțiune de Log In pentru a vedea progresul reclamației. 
4. Ca un utilizator vreau să creez o reclamație pentru a semnala instituțiilor abilitate diferite probleme din oraș. 
5. Ca un utilizator vreau să pot vedea dacă cineva a creat deja o reclamație pentru același deranjament.
6. Ca utilizator vreau să am o notificarea atunci când statusul se schimbă.
7. Ca un utilizator institutie vreau sa vad toate reclamatiile intr-un tabel.
8. Ca un utilizator instituție vreau să pot vedea reclamatiile relevante pe o hartă.
9. Ca un utilizator instituție vreau să modific statusul reclamatiei.
10. Ca un utilizator instituție vreau să trimit mail reclamantilor pentru a cere detalii la nevoie.
11. Ca un utilizator instituție vreau să am posibilitatea de a crea o reclamație în caz de autosesizare.
12. Ca un administrator vreau să pot gestiona conturile, baza de date etc. 
13. Ca un administrator vreau sa pot crea conturi de utilizator institutie.
14. Ca un dezvoltator vreau sa extind acoperirea aplicatiei la nivel national. 
15. Ca un dezvoltator am nevoie sa învăța kotlin.
16. Ca un dezvoltator am nevoie sa învăț sa creez baza de date. 
17. Ca un dezvoltator am nevoie sa invat sa implementez accesul la cameră. 
18. Ca un dezvoltator am nevoie sa invat sa implementez accesul la locație. 
19. Ca un dezvoltator am nevoie de o săptămână pentru a rezolva eventualele erori.

## Backlog Creation:
Pentru a ne organiza task-urile in timpul dezvoltarii aplicatiei am folosit [Trello](https://trello.com/b/XMGkaVW6/civic-guard).
![alt text](https://github.com/RoscaAlexandru775/Civic-Guard/blob/create_complaint/images/trello.png)

## UML Use Case Diagram:
![alt text](https://github.com/RoscaAlexandru775/Civic-Guard/blob/create_complaint/images/UML_Diagram.jpg)

## Bug reporting:
1) Dupa ce un utilizator adauga o reclamatie, afisarea se realiza gresit (fiecare reclamatie era afisata de doua ori, deoarece se afisa atat lista veche, cat si lista actualizata).
   Problema a aparut deoarece, in loc sa stergem lista veche si sa o afisam pe cea actualizata, noi adaugam lista actulizata peste cea veche.
   Solutia: in loc sa asignam RecyclerView.adapter = adapter(updatedList), am folosit recyclerView.adapter?.notifyDataSetChanged().
2) Cand utilizatorul dorea sa adauge o reclamatie, aplicatia se inchidea inainte sa ceara permisiunile pentru camera si locatie,
   dar la a doua incercare aplicatie functia normal. Problema a fost felul in care ceream permisiunile - le ceream la crearea de noi activitati,
   cererea de permisiuni se efectueaza asincron iar functia getLocation() se apela inaintea sa avem permisiunile.
   [Solutia](https://github.com/RoscaAlexandru775/Civic-Guard/commit/5f157895078f379367f65cfa6a4f9d29bb6835e6) a fost sa suprascriem functia onRequestPermissionsResult().

   /**get Permission*/
   //    if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED
   //    && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED)
   
## Build Tool:
Am dezvoltat aplicatia in Android Studio, iar pentru build tool am folosit Gradle, un sistem open-source pentru automatizarea procesului de construire al unei aplicatii.

## Source control:
Branches: https://github.com/RoscaAlexandru775/Civic-Guard/branches
 
Commits: https://github.com/RoscaAlexandru775/Civic-Guard/commits/master

## Refactoring si teste automate:
[Refactorizare](https://github.com/RoscaAlexandru775/Civic-Guard/commit/9045a3ab443a5993120bfe8b60469fd3f116f0e2) - am modificat functia signup() si am adaugat un nou object SignUpValidation, unde am realizat mai multe verificari legate de username, email si parola. Verificarile sunt, astfel, mai usor de citit si inteles. De asemenea, am realizat [9 unit tests](https://github.com/RoscaAlexandru775/Civic-Guard/blob/master/app/src/test/java/com/example/mds/SignUpTest.kt) pentru a ne asigura ca functia returneaza rezultatul corect.
 
![alt text](https://github.com/RoscaAlexandru775/Civic-Guard/blob/create_complaint/images/unit_tests.png)

## Design Patterns
Design Patterns: Aplicațiile Android sunt strâns legate de pattern-ul "Model-View-Controller". In cazul nostru am avut:
- Model - Clasa ComplaintModel
- View - Layout-ul Aplicatiei (activity/complaint.xml)
- Control - ComplaintActivity	
