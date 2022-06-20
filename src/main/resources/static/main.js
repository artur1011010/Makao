class Card {
    constructor(color, value, obj) {
        if (obj !== undefined) {
            obj && Object.assign(this, obj);
        } else {
            this.color = color;
            this.value = value;
        }
    }

    getCardHtml() {
        return `<div class="card ${this.color + this.value}" ${this.getImage(this.color, this.value)}></div>`;
    }

    getImage(color, value) {
        return `style='background-image: url("./img/cards/${color.toLowerCase()}-${value.toLowerCase()}.png");'`;
    }
}

const createPlayer = () => {
    const name = document.getElementById("player-name").value
    if (!name || name.length < 1) {
        alert("imie nie moze byc puste")
        return;
    }

    async function getData(url) {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
        });
        return response.json();
    }

    getData('/api/player?name=' + name)
        .then((data) => {
            console.log("uzytkownik stworzony i dodany do gry z uuid: " + data.uuid)
            setUserUuid(data.uuid);
        });
}

const setUserUuid = (uuid) => {
    localStorage.setItem("uuid", uuid);
}

const getUserUuid = () => {
    return localStorage.getItem("uuid");
}

const getPlayerState = () => {
    async function getData(url) {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                'uuid': getUserUuid()
            },
        });
        return response.json();
    }

    getData('/api/player/state')
        .then(data => {
            console.log(data)
            populateCardsOnHand(data)
        });
}

const getGameState = () => {
    async function getData(url) {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json'
            },
        });
        return response.json();
    }

    getData('/api/game/state')
        .then(data => {
            console.log(data)
            updateGameState(data);
        });
}

const startGame = () => {
    async function getData(url) {
        await fetch(url, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
        });
    }

    getData('/api/game/start')
        .then(() => {
            console.log("rozpoczynanie gry...")
            getGameState();
        });
}

const restartGame = () => {
    async function getData(url) {
        await fetch(url, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
        });
    }

    getData('/api/game/restart')
        .then(() => {
            console.log("rozpoczynanie gry od nowa...")
            getGameState();
        });
}

const updateGameState = (gameStateIn) => {
    const stateText = document.getElementById("game-state")
    stateText.innerHTML = gameStateIn.gameState;
    if (gameStateIn.gameState === "PLAYING") {
        populateLastCardOnStack(gameStateIn.lastOnStack);
    }
    getPlayerState();
}


const populateLastCardOnStack = (card) => {
    const domObj = document.getElementById("last-on-stack")
    const cardd = new Card("1", "2", card)
    console.log(cardd)
    domObj.innerHTML = cardd.getCardHtml();
}

const populateCardsOnHand = (data) => {
    const onHand = document.getElementById("on-hand")
    const cards = data.cardOnHand;
    let result = '';
    if (cards !== undefined && cards.length > 0) {
        cards.forEach(card => {
            const cardd = new Card("1", "2", card)
            result += cardd.getCardHtml();
        })
        onHand.innerHTML = result;
    }
}