$(document).ready(function () {
    var token = localStorage.getItem("token"); 
    var proposta = new URLSearchParams(window.location.search).get('proposta');


    if (!token) {
        alert("Per favore, effettua nuovamente il login.");
        window.location.href = "index.html";
        return;
    }
    
    if (!proposta) {
        alert("La proposta menzionata non è stata trovata.");
        window.location.href = "proposte.html";
        return;
    }


    function formatDate(dateString) {
        const options = { year: 'numeric', month: 'short', day: 'numeric' };
        return new Date(dateString).toLocaleDateString('it-IT', options);
    }

    $("#motivazione").hide();
    $("#currentpage").text("Proposta #"+proposta);

    $.ajax({
        url: "/WMServices_GS/rest/proposte/"+proposta,
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        },
        success: function (data) {
            $('#prop_id').text(data.ID);
            $('#prop_stato').text(data.stato);
            if(data.motivazione != null) {
                $('#prop_mot').text(data.motivazione);
                $("#motivazione").show();
            }
            $('#prop_data').text(formatDate(data.data));
            $('#prop_tecnico').text(data.tecnico.username);

            $("#prop_ric").append(`
                <a href="detail_richiesta.html?richiesta="${data.richiesta.ID}">
                    #${data.richiesta.ID}   
                </a>
            `);

            $('#prod_id').text(data.prodotto.codice);
            $('#prod_marca').text(data.prodotto.nome_produttore);
            $('#prod_nome').text(data.prodotto.nome);
            $('#prod_prezzo').text("€"+data.prodotto.prezzo.toFixed(2).replace('.', ','));
            $('#prod_URL').text(data.prodotto.url);
            $('#prop_note').text(data.note);

            if(data.stato == "In attesa") {
                $("#buttons").append(`
                    <div>
                        <button id="accetta" type="button" class="mainbutton">Accetta</button>
                    </div>
                    <div>
                        <button id="rifiuta" type="button" class="mainbutton" onclick="location.href='rifiuto_proposta.html?proposta=${data.ID}'">Rifiuta</button>
                    </div>
                `);
            }
            
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("Si prega di effettuare l'accesso.");
                window.location.href = "index.html";
                return;   
            }
            else {  
                alert("Errore durante il caricamento dei dettagli della proposta.");
            }                   
        }
    });

    //accetta proposta
    $(document).on('click', '#accetta', function() {
        if (confirm("Sei sicuro di voler accettare questa proposta?")) {
            $.ajax({
                url: "/WMServices_GS/rest/proposte/"+proposta+"/accetta",
                method: "PATCH",
                headers: {
                    "Authorization": "Bearer " + token
                },
                success: function () {
                    window.location.href = "proposte.html";
                },
                error: function (xhr) {
                    if (xhr.status === 401) {
                        alert("Si prega di effettuare l'accesso.");
                        window.location.href = "index.html";
                        return;   
                    }   
                    else {  
                        alert("Errore durante l'accettazione della proposta.");
                    }
                }
            });
        }
    });
});
