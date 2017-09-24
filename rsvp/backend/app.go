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

// [START guest_struct]
type Guest struct {
    Name  string
    LastName string
    Email string
    Company string
}
// [END guest_struct]

// [START list_struct]
type List struct {
    Cursor  string
    Guests 	string
}
// [END list_struct]

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
	w.Header().Set("Access-Control-Allow-Origin", "*")
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
    w.Header().Set("Access-Control-Allow-Origin", "*")
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
	w.Header().Set("Access-Control-Allow-Origin", "*")
	// Create a query for all Person entities.
	const pageSize = 10
	q := datastore.NewQuery("Guest").Limit(pageSize)

	// If the application stored a cursor during a previous request, use it.
	if r.FormValue("cursor") != "" {
	        cursor, err := datastore.DecodeCursor(r.FormValue("cursor"))
	        if err == nil {
	                q = q.Start(cursor)
	        }
	}

	// Iterate over the results.
	var guests []string

	t := q.Run(c)
	for {
	        var g Guest
	        id, err := t.Next(&g)
	        if err == datastore.Done {
	                break
	        }
	        if err != nil {
	                c.Errorf("fetching next Guest: %v", err)
	                break
	        }

   			stringKey := fmt.Sprintf("%v", id)
		    aStringKey := strings.Split(stringKey, ",")

	        res, _ := json.Marshal(g)
	        guests = append(guests, "{\"id\":\"" + aStringKey[1] + "\", \"guest\":" + string(res) + "}")
	}
	// Get updated cursor and store it for next time.
	strGuests := strings.Join(guests, ",")
	c.Debugf("fetching next Guest: %s", strGuests)
	if cursor, err := t.Cursor(); err == nil {	    
		fmt.Fprintf(w, "%s", "{\"cursor\": \"" + cursor.String() +"\", \"guests\":[" + strGuests +"]}")
	}
}
// [END func_list]