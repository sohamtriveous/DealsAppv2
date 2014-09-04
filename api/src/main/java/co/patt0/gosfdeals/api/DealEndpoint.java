package co.patt0.gosfdeals.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static co.patt0.gosfdeals.api.OfyService.ofy;

/**
 * Created by patt0 on 27/08/14.
 */
@Api(name = "dealEndpoint", version = "v1", namespace = @ApiNamespace(ownerDomain = "api.gosfdeals.patt0.co", ownerName = "api.gosfdeals.patt0.co", packagePath = ""))
public class DealEndpoint {
    public DealEndpoint() {
    }

    @ApiMethod(name = "listDeal")
    public CollectionResponse<Deal> listDeal(@Nullable @Named("cursor") String cursorString,
                                             @Nullable @Named("count") Integer count) {

        Query<Deal> query = ofy().load().type(Deal.class);
        if (count != null) query.limit(count);
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        List<Deal> records = new ArrayList<Deal>();
        QueryResultIterator<Deal> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            records.add(iterator.next());
            if (count != null) {
                num++;
                if (num == count) break;
            }

        }

        //Find the next cursor
        if (cursorString != null && cursorString != "") {
            Cursor cursor = iterator.getCursor();
            if (cursor != null) {
                cursorString = cursor.toWebSafeString();
            }
        }
        return CollectionResponse.<Deal>builder().setItems(records).setNextPageToken(cursorString).build();
    }

    @ApiMethod(name = "insertDeal")
    public Deal insertDeal(Deal deal) throws ConflictException {
        //If if is not null, then check if it exists. If yes, throw an Exception
        //that it is already present
        if (deal.getId() != null) {
            if (findRecord(deal.getId()) != null) {
                throw new ConflictException("Object already exists");
            }
        }
        //Since our @Id field is a Long, Objectify will generate a unique value for us
        //when we use put
        ofy().save().entity(deal).now();
        return deal;
    }


    @ApiMethod(name = "updateDeal")
    public Deal updateDeal(Deal deal) throws NotFoundException {
        if (findRecord(deal.getId()) == null) {
            throw new NotFoundException("Deal Record does not exist");
        }
        ofy().save().entity(deal).now();
        return deal;
    }


    @ApiMethod(name = "removeDeal")
    public void removeDeal(@Named("id") Long id) throws NotFoundException {
        Deal record = findRecord(id);
        if (record == null) {
            throw new NotFoundException("Deal Record does not exist");
        }
        ofy().delete().entity(record).now();
    }

    //Private method to retrieve a <code>Quote</code> record
    private Deal findRecord(Long id) {
        return ofy().load().type(Deal.class).id(id).now();
    }

}