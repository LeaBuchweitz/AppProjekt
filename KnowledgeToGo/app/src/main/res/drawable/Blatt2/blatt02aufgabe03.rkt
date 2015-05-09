;; Die ersten drei Zeilen dieser Datei wurden von DrRacket eingefügt. Sie enthalten Metadaten
;; über die Sprachebene dieser Datei in einer Form, die DrRacket verarbeiten kann.
#reader(lib "DMdA-beginner-reader.ss" "deinprogramm")((modname blatt02aufgabe03) (read-case-sensitive #f) (teachpacks ((lib "image2.rkt" "teachpack" "deinprogramm"))) (deinprogramm-settings #(#f write repeating-decimal #f #t none explicit #f ((lib "image2.rkt" "teachpack" "deinprogramm")))))
; Mikulasch, Buchweitz
; Blatt 02
; Aufgabe 03

(define heiner-or
  (lambda (test-1 test-2)
    (if test-1
        #t
        test-2)))

; t f
(heiner-or (= 10 10) (> 2 5))
; f f
(heiner-or (> 23 42) (< 5 2))
; f t
(heiner-or (> 23 42) (< 2 5))
; t t
(heiner-or (< 23 42) (< 2 5))

; richtige or-Implementierung
; t f
(or (= 10 10) (> 2 5))
; f f 
(or (> 23 42) (< 5 2))
; f t 
(or (> 23 42) (< 2 5))
; t t
(or (< 23 42) (< 2 5))

; Eva-Lu hat Recht. In der bereits implementierten or-Anweisung wird nach einem als #t ausgewerteten Ausdruck abgebrochen. (Sichtbar an den blau unterlegten Ausdrücken)
; Alle anderen Tests werden nicht mehr durchgeführt, da die or-Anweisung dem logischen Operator v entspricht und somit ein inklusives "oder" darstellt.
; Es reicht also aus, wenn nur ein einzelner Ausdruck #t ist. 
; An den Testfällen mit dem implementierten or, lässt sich sehen welche Testfälle nicht auf ihren bool'schen Wert getestet werden (blau unterlegt).
; An diesen 2 Stellen verhält sich heiner-or anders, bei seiner Prozedur werden alle Ausdrücke ausgewertet, ohne Abbruch.

