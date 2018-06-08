function addTablerow(contents) {
    let table = document.getElementsByClass("flex-table")[0];
    let row = document.createElement('div');
    row.className = "table-row";
    for(let i = 0; i < contents.length; i++) {
        let dom = document.createElement('div');
        dom.className = "table-row-item";
        dom.innerHTML = contents[i];
        row.appendChild(dom);
    }
    table.appendChild(row);
}

addTablerow("hi","he","hu")
