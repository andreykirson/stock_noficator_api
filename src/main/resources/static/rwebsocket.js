const {
    RSocketClient,
    JsonSerializer,
    IdentitySerializer
} = require('rsocket-core');
const RSocketWebSocketClient = require('rsocket-websocket-client').default;
let client = undefined;

function main() {
    let stockData;

    if (client !== undefined) {
        client.close();
    }

    // Create an instance of a client
    client = new RSocketClient({
        serializers: {
            data: JsonSerializer,
            metadata: IdentitySerializer
        },
        setup: {
            // ms btw sending keepalive to server
            keepAlive: 60000,
            // ms timeout if no keepalive response
            lifetime: 180000,
            // format of `data`
            dataMimeType: 'application/json',
            // format of `metadata`
            metadataMimeType: 'message/x.rsocket.routing.v0',
        },
        transport: new RSocketWebSocketClient({
            url: 'ws://localhost:7001/stocks'
        }),
    });

    // Open the connection
    client.connect().subscribe({
        onComplete: rSocket => {
            rSocket.requestStream({
                metadata: String.fromCharCode('stock-last-data'.length) + 'stock-last-data',
            }).subscribe({
                onComplete: () => console.log('complete'),
                onError: error => {
                    console.log(error);
                },
                onNext: payload => {
                    console.log("OnNext .... ");
                    console.log(JSON.stringify(payload.data));
                    setValue(payload.data);
                },
                onSubscribe: subscription => {
                    subscription.request(2147483647);
                },
            });
        },
        onError: error => {
            console.log(error);
        },
        onSubscribe: cancel => {
            /* call cancel() to abort */
        }
    });
}

function setValue(data) {
    document.getElementById("symbol").innerText = data.symbol;
    document.getElementById("name").innerText = data.name;
    document.getElementById("date").innerText = new Date(data.date).toLocaleTimeString("en-US");
    document.getElementById("open").value = data.open;
    document.getElementById("volume").value = data.volume;
}

window.onload = main();