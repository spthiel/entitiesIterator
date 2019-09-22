function addTablerow(contents) {
    let table = document.getElementsByClassName("flex-table")[0];
    let row = document.createElement('div');
    row.className = "table-row";
    for(let i = 0; i < contents.length; i++) {
        let dom = document.createElement('div');
        dom.className = "table-row-item";
        dom.innerText = contents[i];
        row.appendChild(dom);
    }
    table.appendChild(row);
}

addTablerow(["All entities"]);
addTablerow(["ENTITYTYPE","All entities","Type of the entity."]);
addTablerow(["ENTITIYNAME","All entities","Name of the entity."]);
addTablerow(["ENTITYUUID","All entities","UUID of the entity."]);
addTablerow(["ENTITYXPOSF","All entities","X position of the entity as float."]);
addTablerow(["ENTITYYPOSF","All entities","Y position of the entity as float."]);
addTablerow(["ENTITYZPOSF","All entities","Z position of the entity as float."]);
addTablerow(["ENTITYXPOS","All entities","X position of the entity as integer."]);
addTablerow(["ENTITYYPOS","All entities","Y position of the entity as integer."]);
addTablerow(["ENTITYZPOS","All entities","Z position of the entity as integer."]);
addTablerow(["ENTITYTAG","All entities","NBT of the Entity."]);
addTablerow(["ENTITYDX","All entities","Difference of the x coordinate of the player and the entity."]);
addTablerow(["ENTITYDY","All entities","Difference of the y coordinate of the player and the entity."]);
addTablerow(["ENTITYDZ","All entities","Difference of the z coordinate of the player and the entity."]);
addTablerow(["ENTITY<equipslot>NAME","All entities","Name of the item in the designated armor slot of the entity."]);
addTablerow(["ENTITY<equipslot>ID","All entities","ID of the item in the designated armor slot of the entity."]);
addTablerow(["ENTITY<equipslot>NID","All entities","Numeric ID of the item in the designated armor slot of the entity."]);
addTablerow(["ENTITY<equipslot>DAMAGE","All entities","Damage of the item in the designated armor slot of the entity."]);
addTablerow(["ENTITY<equipslot>COUNT","All entities","Amount of the item in the designated armor slot of the entity."]);
addTablerow(["ENTITY<equipslot>ENCHANTMENTS","All entities","Enchantments of the item in the designated armor slot of the entity."]);
addTablerow(["ENTITYPITCHFROMPLAYER","All entities","Pitch the player has to look at to look at the hight of the entity. (Can be used for look)"]);
addTablerow(["ENTITYYAWFROMPLAYER","All entities","Yaw the player has to look at to look at the hight of the entity. (Can be used for look)"]);
addTablerow(["ENTITYDIR","All entities","Fuzzy direction in which the entity is."]);
addTablerow(["Living entities"]);
addTablerow(["ENTITYPITCH","Living entities","Pitch where the entity is looking at."]);
addTablerow(["ENTITYYAW","Living entities","Yaw where the entity is looking at."]);
addTablerow(["ENTITYHEALTH","Living entities","Health of the entity."]);
addTablerow(["ENTITYMAXHEALTH","Living entities","Max health of the entity."]);
addTablerow(["Items"]);
addTablerow(["ENTITYISITEM","All entities","True if it's an item, false otherwise."]);
addTablerow(["ENTITYITEMAGE","Items","Age of the item."]);
addTablerow(["ENTITYITEMOWNER","Items","Owner of the item. (no clue)"]);
addTablerow(["ENTITYITEMTHROWER","Items","Thrower of the item. (no clue)"]);
addTablerow(["ENTITYITEMNAME","Items","Localized name of the item."]);
addTablerow(["ENTITYITEMUNLOCALIZEDNAME","Items","Unlocalized name of the item."]);
addTablerow(["ENTITYITEMCOUNT","Items","Amount of the item."]);
addTablerow(["ENTITYITEMDISPLAYNAME","Items","Displayname of the item."]);
addTablerow(["ENTITYITEMDAMAGE","Items","Damage of the item."]);
addTablerow(["ENTITYITEMMAXDAMAGE","Items","Max damage of the item."]);
addTablerow(["ENTITYITEMMETADATA","Items","Metadata of the item."]);
addTablerow(["ENTITYITEMENCHANTED","Items","True if the item is enchanted."]);
addTablerow(["ENTITYITEMENSTACKABLE","Items","True if the item is stackable."]);
