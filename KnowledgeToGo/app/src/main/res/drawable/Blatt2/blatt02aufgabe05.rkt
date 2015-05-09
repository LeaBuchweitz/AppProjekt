;; Die ersten drei Zeilen dieser Datei wurden von DrRacket eingefügt. Sie enthalten Metadaten
;; über die Sprachebene dieser Datei in einer Form, die DrRacket verarbeiten kann.
#reader(lib "DMdA-beginner-reader.ss" "deinprogramm")((modname blatt02aufgabe05) (read-case-sensitive #f) (teachpacks ((lib "image2.rkt" "teachpack" "deinprogramm"))) (deinprogramm-settings #(#f write repeating-decimal #f #t none explicit #f ((lib "image2.rkt" "teachpack" "deinprogramm")))))
; Mikulasch, Buchweitz
; Blatt 02
; Aufgabe 05

; Gibt Position eines Fußballspielers anhand einer gegebenen Rückennummer an
(: spieler-position (natural -> (one-of "Torwart" "Abwehr" "Mittelfeld" "Sturm" "Ersatz" "Ungültig")))
(check-expect (spieler-position 1) "Torwart")
(check-expect (spieler-position 3) "Abwehr")
(check-expect (spieler-position 10) "Mittelfeld")
(check-expect (spieler-position 11) "Sturm")
(check-expect (spieler-position 99) "Ersatz")
(check-expect (spieler-position 100) "Ungültig")
(define spieler-position
  (lambda (rückennummer)
    (cond ((= rückennummer 1) "Torwart")
          ((and (>= rückennummer 2) (<= rückennummer 5)) "Abwehr")
          ((or (= rückennummer 6) (= rückennummer 7) (= rückennummer 8) (= rückennummer 10)) "Mittelfeld")
          ((or (= rückennummer 9) (= rückennummer 11)) "Sturm")
          ((and (>= rückennummer 12) (<= rückennummer 99)) "Ersatz")
          (else "Ungültig"))))

(spieler-position 6)

