;; Die ersten drei Zeilen dieser Datei wurden von DrRacket eingefügt. Sie enthalten Metadaten
;; über die Sprachebene dieser Datei in einer Form, die DrRacket verarbeiten kann.
#reader(lib "DMdA-beginner-reader.ss" "deinprogramm")((modname blatt02aufgabe04) (read-case-sensitive #f) (teachpacks ((lib "image2.rkt" "teachpack" "deinprogramm"))) (deinprogramm-settings #(#f write repeating-decimal #f #t none explicit #f ((lib "image2.rkt" "teachpack" "deinprogramm")))))
; Mikulasch, Buchweitz
; Blatt 02
; Aufgabe 04

; Berechnet das Minimum aus 2 Zahlen
(: minimum (real real -> real))
(check-within (minimum 3 4) 3 0.0001)
(check-within (minimum 6 2) 2 0.0001)
(define minimum
  (lambda (zahl-1 zahl-2)
    (* 0.5 (- (+ zahl-1 zahl-2) (abs (- zahl-1 zahl-2)))))) 

; Berechnet das Maximum aus 2 Zahlen
(: maximum (real real -> real))
(check-within (maximum 3 4) 4 0.0001)
(check-within (maximum 6 2) 6 0.0001)
(define maximum
  (lambda (zahl-1 zahl-2)
    (* 0.5 (+ (+ zahl-1 zahl-2) (abs (- zahl-1 zahl-2))))))

; Berechnet eine Zahl in eine obere und eine untere Intervallgrenze einbettet
(: clamp (real real real -> real))
(check-within (clamp 2 1 3) 2 0.0001)
(check-within (clamp 4 1 3) 3 0.0001)
(check-within (clamp 1.5 1 3) 1.5 0.0001)
(define clamp
  (lambda (x untergrenze obergrenze)
    (maximum untergrenze (minimum x obergrenze))))

(clamp 2 1 4)
(clamp 0 1 4)
(clamp 5 1 4)
