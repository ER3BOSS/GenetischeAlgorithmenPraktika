# Praktikum genetische Algorithmen

## Fitness Function
- nicht 0
- i.d.r positiv
- double

-> Faltung bewerten: hydophob Paare, die nicht in der Sequenz benachbart sind; jede Überlappung -> schlecht;

=> Formel für den Fitnesswert

= ((1 + HYDOPHOB_PAARE) * 1) / ((1 + UEBERLAPPUNG) * 10)

## Features
- Anzeige des besten Ergebnisses von jeder Generation
- Anzeige des besten Kanidatens am Ende
- Log-Datei für jede Generation -> für Exel (Deutsches Zahlenformat)

## 3. Termin
- Ziel: einfacher genetischer Algorithmus

## 4. Termin
- Auswahl eines anderen Selectionsverahrens
- Dynamische steuerung der Mutationsrate

## 5. Termin
- Algorithmus mit größerem Benchmark
-> Verhalten des Algorithmus
-> Abfrage des Quellcodes