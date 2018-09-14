var members = document.getElementsByClassName("member-block");

var players = [];
for (var i = 0; i < members.length; i++) {
    var foo = members.item(i).getElementsByClassName("title").item(0);
    var img = members.item(i).getElementsByClassName("lazy").item(0);

    if (img) {
        players.push({
            displayName: foo.innerText,
            imageUrl: img.src,
            userName: { value: img.id }
        });
    }
}

console.log(JSON.stringify(players));