| päivä    | aika    | mitä tein                                           |
| -------- | ------- | --------------------------------------------------- |
| 9.11.    | 3h      | päätetty projektin aihe ja tehty vaatimusmäärittely |
| 16.11.   | 4h      | luotu maven-projekti, alettu toteuttamaan vaadittavia luokkia, tehty niille muutamia perusfunktioita, lisätty yksi testitiedosto ja sinne alkeelliset testit |
|  17.11.  | 5h      | muokattu luokkia ja korjattu suunnitelmaa: korvattu osa eilen tehdyistä luokista uusilla ja tehty näille funktioita ja testit                                                    |
| 23.11. |  8h | Säätöä fxml:n käyttöönotossa, testibranch myös sovellukselle ilman fxml:ää
| 24.11. | 5h | Aloitettu pääikkunan suunnittelua scenebuilderilla, lisätty joitain luokkia, testejä ja metodeja
| 25.11. | 6.5h | Tunkattu ohjelma siten että sen saa ajettua käyttöliittymän kanssa, ei juuri muuta |
| 27.11. | 7h | Toteutettu rivin lisäys tableView-taulukkoon, tehty tarkistimet joitakin inputeja varten, tehty joitain muokkauksia fxml:ssä |
| 30.11. | 7h | Toteutettu kuittien lisäys tableView-taulukkoon ja säädetty fxml:ää sen mukaisesti. Vaihdettu myös nappien asettelua. Alettu toteuttaa rivien editointia, se kuitenkin vaatii vielä suunnittelua. |
| 1.12. | 7h | Aloitettu tallennustoiminnallisuuden toteutusta, korjailtu käyttöliittymää ja sen koodia |
| 3.12. | 9h | Selvitelty aika monta tuntia miksi tableViewit ei toimi odotetusti, lisäksi epäselvää miksi välillä jostain tulee concurrentModificationException. Lopulta hylätty koko branch ja aloitettu käyttämään tableViewejä huolellisemmin |
| 4.12. | 7h | Jatkettu eilen valittua linjaa, ja nyt toteutettu uudestaan tuotteen lisääminen, muokkaaminen ja poistaminen, ja aloitettu kuitin näyttämistä ja muokkaamista. |
| 8.12. | 8h | Aloitettu tallennustoiminnallisuuden rakentamista sql:llä. Toteutettu joitain koodikatselmuksessa saatuja korjausehdotuksia. Tehty muita pieniä korjauksia sovellukseen. Korjattu testien ajamisen kanssa ollut ongelma, ja eriytetty käyttöliittymän rakennus omaan pakettiin. | 
| 10.12. | 5h | Jatkettu tallennustoiminnallisuuden toteuttamista: nyt kuittien tallentaminen kuittitauluun on jotenkin toteutettu. Kuitit luetaan sovelluksen käynnistyessä tietokannasta ja näytetään käyttöliittymässä.
| 10.12. | 5h | Jatkettu tallennustoiminnallisuuden toteuttamista: nyt kuittien tallentaminen kuittitauluun on jotenkin toteutettu. Kuitit luetaan sovelluksen käynnistyessä tietokannasta ja näytetään käyttöliittymässä. |
| 14.12. | 10h | Tehty loppuun daon tallennus ja aloitettu tekemään sille testejä. Testien ajaminen johtanut kuitenkin aina SQL:n `database locked`-erroriin. Aiheuttaja ei selvinnyt joten palattu aiempaan toimivaan versioon ja aloitettu daon ja sen testien kirjoittaminen uudestaan |
| 15.12. | 5h | Jatkettu daon uudelleenkirjoittamista. Vaihdettu sovelluksen tallennustapaa: jokainen muutos tallennetaan, käyttäjä ei voi valita milloin tallennetaan. |
| 16.12. | 6h | Muokattu ReceiptServiceä, kirjoitettu uudestaan vanhoja daon metodeita ja laitettu ReceiptService kutsumaan niitä aina kun joku muuttuu. |
| 17.12. | 10h | Kirjoitettu loppuun tarvittavat metodit daoon testeineen. Muokattu joitain jo tehtyjä metodeja. |
| 18.12. | 10h | Korjattu checkstyle-virheitä, dokumentoitu koodia, tehty virheilmoituksista käyttäjäystävällisempiä. Käyty läpi asioita palautusta varten. |
| yht. | 122.5h |
