/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-remote/LICENSE.txt for details.
*/
package org.LexGrid.LexBIG.distributed.test.function.query;

// LexBIG Test ID: T1_FNC_10	TestQuerybyRelationshipDomain

import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.testUtil.LexEVSServiceHolder;
import org.LexGrid.LexBIG.testUtil.ServiceTestCase;

/**
 * The Class TestQuerybyRelationshipDomain.
 */
public class TestQuerybyRelationshipDomain extends ServiceTestCase
{
    final static String testID = "testQuerybyRelationshipDomain";

    @Override
    protected String getTestID()
    {
        return testID;
    }

    /**
     * Test queryby relationship domain.
     * 
     * @throws LBException the LB exception
     */
    public void testQuerybyRelationshipDomain() throws LBException
    {

//        String domain = "Anatomy_Kind";
        String domainCode = "C6739"; //"Anatomy_Kind";
        // check if the supplied domain is valid

        ConvenienceMethods cm = new ConvenienceMethods(LexEVSServiceHolder.instance().getLexEVSAppService());

        CodedNodeGraph cng = LexEVSServiceHolder.instance().getLexEVSAppService().getNodeGraph(THES_SCHEME, null, null);

        cng = cng.restrictToTargetCodes(cm.createCodedNodeSet(new String[]{domainCode}, THES_SCHEME, null));

        ConceptReference cref = Constructors.createConceptReference(domainCode, THES_SCHEME);
        assertTrue(cng.isCodeInGraph(cref).booleanValue());

        // now we have validated that the value supplied is a domain. The answer to the test is the graph
        // that
        // is focused on that code (domainCode)

        // I'll go down two levels for the heck of it.
        cng = LexEVSServiceHolder.instance().getLexEVSAppService().getNodeGraph(THES_SCHEME, null, "roles");

        ResolvedConceptReference[] rcr = cng
                .resolveAsList(Constructors.createConceptReference(domainCode, THES_SCHEME), false, true, -1, 2, null,
                               null, null, 0).getResolvedConceptReference();

        // focus
        assertTrue(rcr.length == 1);
        assertTrue(rcr[0].getConceptCode().equals(domainCode));

        Association[] a = rcr[0].getTargetOf().getAssociation();

        // one level deep
        assertTrue("Length: " + a.length ,a.length == 2);
        assertTrue(contains(a, "subClassOf", "C45747"));
        assertTrue(contains(a, "subClassOf",  "C49179"));
    }

    private boolean contains(Association[] a, String association, String conceptCode)
    {
        boolean found = false;
        for (int i = 0; i < a.length; i++)
        {
            if (a[i].getAssociationName().equals(association)
                    && contains(a[i].getAssociatedConcepts().getAssociatedConcept(), conceptCode))
            {
                found = true;
                break;
            }
        }

        return found;
    }

    private boolean contains(AssociatedConcept[] ac, String conceptCode)
    {
        boolean found = false;
        for (int i = 0; i < ac.length; i++)
        {
            if (ac[i].getConceptCode().equals(conceptCode))
            {
                found = true;
                break;
            }

        }

        return found;
    }

}