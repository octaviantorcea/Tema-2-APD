# Tema 2 APD 2021 - Map-Reduce

Torcea Octavian 334CA

## Flow-ul programului
* se citesc datele de intrare (nr. de workeri, inputFile si outputFile);
* din fisierul de intrare se extrage path-ul catre documentele ce trebuie
analizate;
* se memoreaza numele documentelor si dimensiunea acestora;
* se initializeaza thread pool-ul pentru operatiile de Map cu un nr de
thread-uri egal cu nr de workeri;
* se initializeaza task-uri de tip Map pentru a fi executate de thread-uri;
* un MapTask primeste ca argumente:
	* calea catre document;
	* offset-ul din fisier de la care trebuie sa citeasca octetii;
	* nr de octeti ce trebuie sa-l citeasca din fisier;
	* dimensiunea totala a documentului;
* viitoarele rezultate obtinute in urma executarii tasku-urilor sunt
retinute intr-un array;
* rezultatul in urma operatiei de Map contine:
	* numele fisierului;
	* un array ce contine cuvantul/cuvintele cele mai lungi din fragmentul
		  analizat
	* un dictionar in care cheile sunt dimensiunile cuvintelor, iar valorile
          sunt nr de cuvinte cu dimensiunea respectiva;

* se initializeaza thread pool-ul pentru operatiile de Reduce cu un nr de
thread-uri egal cu nr de workeri;
* pentru fiecare document se initializeaza un task de tip Reduce;
* un task de tip Reduce primeste ca argument lista de rezultate
corespunzatoare fisierului caruia i-a fost atribuit;
* viitoarele rezultate de tip Reduce sunt adaugate intr-un array;
* rezultatul in urma operatiei de Reduce contine:
	* numele documentului;
	* rangul;
	* lungimea cuvantului/cuvintelor cu dimensiunea maxima
	* nr de cuvinte de au lungimea maxima;

* rezultatele sunt apoi sortate descrescator in functie de rang si scrise in
fisierul de iesire.
