$(document).ready(function () {
    var token = localStorage.getItem("token"); 
    var richiesta = new URLSearchParams(window.location.search).get('richiesta');


    if (!token) {
        alert("Per favore, effettua nuovamente il login.");
        window.location.href = "index.html";
        return;
    }
    
    if (!richiesta) {
        alert("La richiesta menzionata non è stata trovata.");
        window.location.href = "richieste.html";
        return;
    }


    function formatDate(dateString) {
        const options = { year: 'numeric', month: 'short', day: 'numeric' };
        return new Date(dateString).toLocaleDateString('it-IT', options);
    }

    $("#motivazione").hide();
    $("#ric_prop").hide();
    $("#currentpage").text("Richiesta #"+richiesta);

    $.ajax({
        url: "/WMServices_GS/rest/richieste/"+richiesta,
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        },
        success: function (data) {
            $('#ric_id').text(data.ID);
            $('#ric_stato').text(data.stato);
            $('#ric_data').text(formatDate(data.data));
            $('#ric_cat').text(data.categoria);
            $.each(data.caratteristiche, function(chiave, valore) {
                const ric_car = $("#ric_car");
                const caratteristica = `
                    <div class="w3-row">
                        <div class="w3-half w3-container w3-padding detail-label">${chiave}:</div>
                        <div class="w3-half w3-container w3-padding detail-value">${valore}</div>
                    </div>
                `;
                ric_car.append(caratteristica);
            });
            $('#ric_note').text(data.note);

            if(data.proposta!= null) {
                $("#ric_prop").show();

                $('#prop_id').text(data.proposta.ID);
                $('#prop_stato').text(data.proposta.stato);
                if(data.proposta.motivazione != null) {
                    $('#prop_mot').text(data.proposta.motivazione);
                    $("#motivazione").show();
                }
                $('#prop_data').text(formatDate(data.proposta.data));
                $('#prop_tecnico').text(data.tecnico_incarico.username);
                $('#prod_id').text(data.proposta.prodotto.codice);
                $('#prod_marca').text(data.proposta.prodotto.nome_produttore);
                $('#prod_nome').text(data.proposta.prodotto.nome);
                $('#prod_prezzo').text("€"+data.proposta.prodotto.prezzo.toFixed(2).replace('.', ','));
                $('#prod_URL').text(data.proposta.prodotto.url);
                $('#prop_note').text(data.proposta.note);
            }
            
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("Si prega di effettuare l'accesso.");
                window.location.href = "index.html";
                return;   
            }
            else {  
                alert("Errore durante il caricamento dei dettagli della richiesta.");
            }                   
        }
    });

    //elimina richiesta
    $('#elimina').click(function () {
        if (confirm("Sei sicuro di voler eliminare questa richiesta?")) {
            $.ajax({
                url: "/WMServices_GS/rest/richieste/"+richiesta,
                method: "DELETE",
                headers: {
                    "Authorization": "Bearer " + token
                },
                success: function () {
                    window.location.href = "richieste.html";
                },
                error: function (xhr) {
                    if (xhr.status === 401) {
                        alert("Si prega di effettuare l'accesso.");
                        window.location.href = "index.html";
                        return;   
                    }   
                    else {  
                        alert("Errore durante l'eliminazione della richiesta.");
                    }
                }
            });
        }
    });
});
