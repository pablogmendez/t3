package app

import (
	"fmt"
	"strconv"
	"strings"
	"encoding/json"
	"net/http"
    "appengine"
    "appengine/datastore"
    "appengine/memcache"
)

// [START greeting_struct]
type Guest struct {
    Name  string
    LastName string
    Email string
    Company string
}
// [END greeting_struct]

func init() {
	http.HandleFunc("/confirm", confirm)
	http.HandleFunc("/query", query)
	http.HandleFunc("/list", list)
}

// [START func_confirm]
func confirm(w http.ResponseWriter, r *http.Request) {
    // [START new_context]	
	c := appengine.NewContext(r)
	// [END new_context]        
    c.Debugf("Entrando a la funcion confirm del servicio default: %v", r.URL)

	g := Guest{
	    Name: r.FormValue("name"),
	    LastName: r.FormValue("lastname"),
	    Email: r.FormValue("email"),
	    Company: r.FormValue("company"),
	}

    //key := datastore.NewIncompleteKey(c, "Guest", datastore.NewKey(c, "Guest", "default_gest", 0, nil))
    key := datastore.NewIncompleteKey(c, "Guest", nil)
    id, err := datastore.Put(c, key, &g)
    if err != nil {
            http.Error(w, err.Error(), http.StatusInternalServerError)
            return
    }
    stringKey := fmt.Sprintf("%v", id)
    aStringKey := strings.Split(stringKey, ",")
	res, _ := json.Marshal(map[string]string{"id": aStringKey[1]})
    fmt.Fprintf(w, "%s", string(res))
}
// [END func_confirm]

// [START func_query]
func query(w http.ResponseWriter, r *http.Request) {
	var g Guest

    // [START new_context]	
    c := appengine.NewContext(r)
    id := r.FormValue("id")
	n, err := strconv.ParseInt(id, 10, 64)
	if err != nil {
    	fmt.Fprint(w, "Unable to parse key")
 	   	return;
	}
    
	// Primero busco la entity en la memcache; si no esta, 
	// busco en el datastore
	_, err1 := memcache.Gob.Get(c, id, &g)
	c.Debugf("Resultado del cacheo %s", err1)
	if err1 != nil {
		c.Debugf("No fue un hit")
	    key := datastore.NewKey(c, "Guest", "", n, nil)
	    
	    if err := datastore.Get(c, key, &g); err != nil {
	        http.Error(w, err.Error(), http.StatusInternalServerError)
	        return
	    }

	    // Salvo el resultado en la memcache
		item1 := &memcache.Item{
		    Key:   id,
		    Object: g,
		}
		memcache.Gob.Set(c, item1);
	}
	// Devuelvo la entity encontrada
	res, _ := json.Marshal(g)
    fmt.Fprintf(w, "%s", string(res))
}
// [END func_query]

// [START func_list]
func list(w http.ResponseWriter, r *http.Request) {
    // [START new_context]	
    c := appengine.NewContext(r)

	n, err := strconv.ParseInt("5171003185430528", 10, 64)

	//entity_id_int, err := strconv.ParseInt(entity_id, 10, 64) 
	if err != nil {
    	fmt.Fprint(w, "Unable to parse key")
 	   	return;
	}
    
    var g Guest
    key := datastore.NewKey(c, "Guest", "", n, nil)
    
    if err := datastore.Get(c, key, &g); err != nil {
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
    }

    fmt.Fprintf(w, "Stored and retrieved the Employee named %q", g.Name)
}
// [END func_list]