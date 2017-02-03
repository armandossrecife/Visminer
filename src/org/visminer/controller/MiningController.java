package org.visminer.controller;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.repositoryminer.codemetric.CodeMetricId;
import org.repositoryminer.exceptions.RepositoryMinerException;
import org.repositoryminer.mining.RepositoryMiner;
import org.repositoryminer.model.Reference;
import org.repositoryminer.parser.java.JavaParser;
import org.repositoryminer.scm.ISCM;
import org.repositoryminer.scm.SCMFactory;
import org.repositoryminer.scm.SCMType;
import org.visminer.request.MiningRequest;
import org.visminer.util.MetricFactory;

@Path("mining")
public class MiningController {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("get-references")
	public List<Reference> getReferences(@QueryParam("scm") String scmCode, @QueryParam("path") String path) {
		try {
			SCMType scmType = SCMType.valueOf(scmCode);
			ISCM scm = SCMFactory.getSCM(scmType);

			scm.open(path);
			List<Reference> references = scm.getReferences();
			scm.close();

			return references;
		} catch (NullPointerException |  RepositoryMinerException e) {
			throw new WebApplicationException(e.getMessage(), 400);
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("mine")
	public Response mine(MiningRequest request) {
		RepositoryMiner rm = new RepositoryMiner(request.getPath(), request.getName(), request.getDescription(), request.getScm());

		rm.addParser(new JavaParser());

		for (Reference ref : request.getReferences()) {
			rm.addReference(ref.getName(), ref.getType());
		}

		for (CodeMetricId metric : request.getMetrics()) {
			rm.addDirectCodeMetric(MetricFactory.getMetric(metric));
		}

		try {
			rm.mine();
			return Response.status(Response.Status.OK).build();
		} catch (IOException e) {
			throw new WebApplicationException(e.getMessage(), 400);
		}

	}

}